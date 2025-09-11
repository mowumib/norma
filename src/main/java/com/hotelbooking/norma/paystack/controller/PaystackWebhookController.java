package com.hotelbooking.norma.paystack.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.hotelbooking.norma.email.EmailService;
import com.hotelbooking.norma.email.dto.SendEmailRequest;
import com.hotelbooking.norma.enums.BookingStatus;
import com.hotelbooking.norma.enums.PaymentStatus;
import com.hotelbooking.norma.paystack.enums.PaystackPaymentStatus;
import com.hotelbooking.norma.paystack.repository.TransactionRepository;
import com.hotelbooking.norma.paystack.service.PaystackService;
import com.hotelbooking.norma.repository.BookingRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/paystack")
public class PaystackWebhookController {

    // @Value("${paystack.secret-key}")    
    private String paystackSecretKey = "sk_test_322cba52c7a40d8e12d59c3d5fb90a5050399f73";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final PaystackService paystackService;
    private final EmailService emailService;
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request, @RequestHeader("x-paystack-signature") String paystackSignature) throws IOException {
        
        byte[] payloadBytes = IOUtils.toByteArray(request.getInputStream());

        // Convert byte array to string for logging and JSON parsing (after verification).
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        System.out.println("Raw payload: " + payload);
        System.out.println("Payload signature: " + paystackSignature);

        // Verify the signature using the raw byte array
        if (!verifySignature(payloadBytes, paystackSignature)) {
            System.err.println("Webhook signature verification failed. Ignoring request.");
            return new ResponseEntity<>("Invalid signature", HttpStatus.UNAUTHORIZED);
        }
        
        try {
            // Step 3: Parse the JSON payload and process the event
            JsonNode rootNode = objectMapper.readTree(payload);
            String eventType = rootNode.path("event").asText();

            
            // The 'charge.success' event is the most common one to handle for payments
            if ("charge.success".equals(eventType)) {
                JsonNode dataNode = rootNode.path("data");
                String reference = dataNode.path("reference").asText();
                paystackService.verifyTransaction(reference);
                transactionRepository.findByReference(reference).ifPresent(tx -> {
                    bookingRepository.findByBookingCode(tx.getBookingCode()).ifPresent(booking -> {
                        String paidAtString = dataNode.path("paid_at").asText();
                        // Define a formatter for the ISO 8601 date format from Paystack
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        // Parse the string into a LocalDateTime object
                        LocalDateTime paidAt = LocalDateTime.parse(paidAtString, formatter);
                        tx.setPaidAt(paidAt);
                        tx.setPaystackPaymentStatus(PaystackPaymentStatus.SUCCESS);
                        booking.setPaymentStatus(PaymentStatus.PAID);
                        booking.setBookingStatus(BookingStatus.COMPLETED); // Set to CONFIRMED
                        transactionRepository.save(tx);
                        bookingRepository.save(booking);

                        Map<String, String> placeholders = Map.of(                
                        "userName", booking.getUser().getName(),
                        "hotelName", booking.getHotel().getName(),
                        "bookingCode", booking.getBookingCode(),
                        "checkInDate", booking.getCheckInDate().toString(),
                        "checkOutDate", booking.getCheckOutDate().toString(),
                        "roomType", booking.getRoom().getRoomType().toString(),
                        "roomNumber", booking.getRoom().getRoomNumber());
                        
                        SendEmailRequest emailRequest = new SendEmailRequest(
                        booking.getUser().getEmail(),
                        "ROOM BOOKING CONFIRMATION",
                        "room-booking-confirmation",
                        placeholders);
                        emailRequest.setPlaceholders(placeholders);

                        emailService.sendTemplatedEmail(emailRequest);
                    
                    });
                });
                
            } else {
                System.out.println("Received unhandled event type: " + eventType);
            }

        } catch (Exception e) {
            System.err.println("Error processing webhook payload: " + e.getMessage());
            return new ResponseEntity<>("Processing error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Webhook received and processed", HttpStatus.OK);
    }

    /**
     * Verifies the request signature using the secret key.
     *
     * @param payload The raw JSON payload from the request body.
     * @param paystackSignature The signature from the 'x-paystack-signature' header.
     * @return true if the signature is valid, false otherwise.
     */
    private boolean verifySignature(byte[] payloadBytes, String paystackSignature){
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

            // Compare the calculated signature with the signature from Paystack
            return calculatedSignature.equals(paystackSignature);

        } catch (Exception e) {
            System.err.println("Error during signature calculation: " + e.getMessage());
            return false;
        }
    }
}
