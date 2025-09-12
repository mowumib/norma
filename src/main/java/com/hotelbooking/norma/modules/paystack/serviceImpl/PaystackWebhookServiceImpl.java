package com.hotelbooking.norma.modules.paystack.serviceImpl;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.hotelbooking.norma.entity.Room;
import com.hotelbooking.norma.enums.BookingStatus;
import com.hotelbooking.norma.enums.PaymentStatus;
import com.hotelbooking.norma.enums.RoomStatus;
import com.hotelbooking.norma.modules.booking.repository.BookingRepository;
import com.hotelbooking.norma.modules.email.EmailService;
import com.hotelbooking.norma.modules.email.dto.SendEmailRequest;
import com.hotelbooking.norma.modules.paystack.enums.PaystackPaymentStatus;
import com.hotelbooking.norma.modules.paystack.repository.TransactionRepository;
import com.hotelbooking.norma.modules.paystack.service.PaystackService;
import com.hotelbooking.norma.modules.paystack.service.PaystackWebhookService;
import com.hotelbooking.norma.modules.room.repository.RoomRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaystackWebhookServiceImpl implements PaystackWebhookService {

    @Value("${paystack.secret-key}")
    private String paystackSecretKey;
    
    private final PaystackService paystackService;

    private final TransactionRepository transactionRepository;

    private final BookingRepository bookingRepository;

    private final EmailService emailService;

    private final ObjectMapper objectMapper;

    private final RoomRepository roomRepository;

    /**
     * Handles the incoming Paystack webhook request.
     * <p>
     * This method receives a webhook from Paystack, verifies its signature using the provided
     * secret key, and processes the payload if the signature is valid. If the signature is invalid
     * or an error occurs during processing, an appropriate response is returned.
     * </p>
     *
     * @param request The HTTP request containing the webhook payload. This is used to read
     *                the raw input stream containing the Paystack event data.
     * @param paystackSignature The signature from the 'x-paystack-signature' header.
     *                          This is used to verify the authenticity of the incoming webhook.
     * @return A {@link ResponseEntity} with an HTTP status and message. If the signature is valid
     *         and the payload is processed successfully, an HTTP 200 OK response is returned.
     *         If the signature is invalid, an HTTP 401 Unauthorized response is returned.
     *         If an error occurs during payload processing, an HTTP 500 Internal Server Error
     *         response is returned.
     * @throws IOException If an error occurs while reading the request input stream or processing
     *                     the payload.
     */
    @Override
    public ResponseEntity<String> handleWebhook(HttpServletRequest request, String paystackSignature) throws IOException {
        byte[] payloadBytes = IOUtils.toByteArray(request.getInputStream());

        // Verify the signature
        if (!verifySignature(payloadBytes, paystackSignature)) {
            System.err.println("Webhook signature verification failed. Ignoring request.");
            return new ResponseEntity<>("Invalid signature", HttpStatus.UNAUTHORIZED);
        }

        try {
            processWebhookPayload(payloadBytes);
        } catch (Exception e) {
            System.err.println("Error processing webhook payload: " + e.getMessage());
            return new ResponseEntity<>("Processing error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Webhook received and processed", HttpStatus.OK);
    }

    /**
     * Verifies the request signature using the secret key.
     * <p>
     * This method compares the computed signature from the request's payload and the
     * provided 'x-paystack-signature' header to ensure data integrity and authenticity
     * of the webhook request.
     * </p>
     *
     * @param payload The raw JSON payload from the request body. This is used to compute
     *                the signature for comparison.
     * @param paystackSignature The signature from the 'x-paystack-signature' header
     *                          in the incoming webhook request.
     * @return true if the signature is valid (i.e., the computed signature matches the
     *         provided signature), false otherwise.
     */
    private boolean verifySignature(byte[] payloadBytes, String paystackSignature) {
        if (paystackSecretKey == null || paystackSecretKey.isEmpty()) {
            System.err.println("Paystack secret key is not configured.");
            return false;
        }

        try {
            String calculatedSignature = Hashing.hmacSha512(paystackSecretKey.getBytes(StandardCharsets.UTF_8))
                .hashBytes(payloadBytes)
                .toString();

            System.out.println("Calculated signature: " + calculatedSignature);
            System.out.println("Paystack signature: " + paystackSignature);

            return calculatedSignature.equals(paystackSignature);

        } catch (Exception e) {
            System.err.println("Error during signature calculation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Processes the Paystack webhook event payload asynchronously.
     * <p>
     * This method parses the incoming JSON payload, verifies the event type, and
     * processes the corresponding event (e.g., handling a successful charge).
     * </p>
     *
     * @param payloadBytes The raw JSON payload from the Paystack webhook.
     * @throws IOException If there's an error while parsing the JSON payload or processing
     *                     the event.
     */
    @Async
    public void processWebhookPayload(byte[] payloadBytes) throws IOException {
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        System.out.println("Raw payload: " + payload);

        JsonNode rootNode = objectMapper.readTree(payload);
        String eventType = rootNode.path("event").asText();

        if ("charge.success".equals(eventType)) {
            JsonNode dataNode = rootNode.path("data");
            String reference = dataNode.path("reference").asText();
            paystackService.verifyTransaction(reference);

            transactionRepository.findByReference(reference).ifPresent(tx -> {
                bookingRepository.findByBookingCode(tx.getBookingCode()).ifPresent(booking -> {
                    String paidAtString = dataNode.path("paid_at").asText();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    LocalDateTime paidAt = LocalDateTime.parse(paidAtString, formatter);

                    tx.setPaidAt(paidAt);
                    tx.setPaystackPaymentStatus(PaystackPaymentStatus.SUCCESS);
                    booking.setPaymentStatus(PaymentStatus.PAID);
                    booking.setBookingStatus(BookingStatus.BOOKED);
                    Room room = booking.getRoom();
                    room.setRoomStatus(RoomStatus.BOOKED);

                    transactionRepository.save(tx);
                    roomRepository.save(room);
                    bookingRepository.save(booking);

                    Map<String, String> placeholders = Map.of(
                        "username", booking.getUser().getName(),
                        "hotelName", booking.getHotel().getName(),
                        "bookingCode", booking.getBookingCode(),
                        "checkInDate", booking.getCheckInDate().toString(),
                        "checkOutDate", booking.getCheckOutDate().toString(),
                        "roomType", room.getRoomType().toString(),
                        "roomNumber", room.getRoomNumber()
                    );

                    SendEmailRequest emailRequest = new SendEmailRequest(
                        booking.getUser().getEmail(),
                        "ROOM BOOKING CONFIRMATION",
                        "room-booking-confirmation",
                        placeholders
                    );
                    emailRequest.setPlaceholders(placeholders);

                    emailService.sendTemplatedEmail(emailRequest);
                });
            });
        } else {
            System.out.println("Received unhandled event type: " + eventType);
        }
    }
}
