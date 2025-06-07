package com.hotelbooking.norma.paystack.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.entity.Booking;
import com.hotelbooking.norma.entity.User;
import com.hotelbooking.norma.enums.PaymentStatus;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentDto;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentRequest;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentResponse;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentVerificationResponse;
import com.hotelbooking.norma.paystack.dto.PaystackTransactionFetchResponse;
import com.hotelbooking.norma.paystack.dto.PaystackTransactionListResponse;
import com.hotelbooking.norma.paystack.entity.Transaction;
import com.hotelbooking.norma.paystack.entity.TransactionVerification;
import com.hotelbooking.norma.paystack.enums.PaystackPaymentStatus;
import com.hotelbooking.norma.paystack.repository.TransactionRepository;
import com.hotelbooking.norma.paystack.repository.TransactionVerificationRepository;
import com.hotelbooking.norma.paystack.service.PaystackService;
import com.hotelbooking.norma.repository.BookingRepository;
import com.hotelbooking.norma.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaystackServiceImpl implements PaystackService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final TransactionVerificationRepository transactionVerificationRepository;
    private final TransactionRepository transactionRepository;

    @Value("${paystack.secret-key}")
    private String secretKey; 
    
    @Value("${paystack.base-url}")
    private String paystackBaseUrl;

    @SuppressWarnings("null")
    @Override
    public ResponseModel initializeTransaction(PaystackPaymentDto dto) {
        String url =  paystackBaseUrl + "/transaction/initialize";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(secretKey.trim());

        Booking booking = bookingRepository.findByBookingCode(dto.getBookingCode()).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));   

        User user = userRepository.findByUserCode(dto.getUserCode()).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));

        if(!booking.getUser().getUserCode().equals(user.getUserCode())){
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Booking not associated with user.", null);
        }

        PaystackPaymentRequest request = new PaystackPaymentRequest();
        request.setEmail(user.getEmail());
        request.setAmount(booking.getAmount() * 100);
        request.setCurrency("NGN");

        HttpEntity<PaystackPaymentRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<PaystackPaymentResponse> paystackResponse = restTemplate.postForEntity(url, entity, PaystackPaymentResponse.class);
        
        if (!paystackResponse.getStatusCode().is2xxSuccessful()) {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Transaction initialization failed", null);
        }

        PaystackPaymentResponse response = paystackResponse.getBody();

        Transaction transaction = new Transaction();
        transaction.setEmail(user.getEmail());
        transaction.setReference(response.getData().getReference());
        transaction.setAmount(dto.getAmount());
        transaction.setPaidAt(null);
        transaction.setInitiatedAt(LocalDateTime.now());
        transaction.setChannel("Paystack");
        transaction.setPaystackPaymentStatus(PaystackPaymentStatus.PENDING);
        transaction.setBookingCode(dto.getBookingCode());
        transaction.setUserCode(dto.getUserCode());

        transactionRepository.save(transaction);
        return new ResponseModel(HttpStatus.OK.value(), "Transaction initialization successful", response);
        
    }

    @Override
    public ResponseModel verifyTransaction(String reference) {
        String url = paystackBaseUrl + "/transaction/verify/" + reference;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PaystackPaymentVerificationResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PaystackPaymentVerificationResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Transaction verification failed", null);
        }

        PaystackPaymentVerificationResponse verificationResponse = response.getBody();

        if (verificationResponse == null || !verificationResponse.isStatus()
                || verificationResponse.getData() == null
                || !"success".equalsIgnoreCase(verificationResponse.getData().getStatus())) {

            transactionRepository.findByReference(reference).ifPresent(tx -> {
                userRepository.findByEmail(tx.getEmail()).ifPresent(user -> {
                    tx.setPaystackPaymentStatus(PaystackPaymentStatus.FAILED);
                    transactionRepository.save(tx);
                });
            });

            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Transaction verification failed", verificationResponse);
        }

        // If already verified
        if (transactionVerificationRepository.findByTransactionReference(reference).isPresent()) {
            return new ResponseModel(HttpStatus.OK.value(), "Transaction already verified", verificationResponse);
        }

        // Save verification
        var data = verificationResponse.getData();
        TransactionVerification verification = new TransactionVerification();
        verification.setTransactionReference(reference);
        verification.setTransactionId(data.getId());
        verification.setFirstName(data.getCustomer().getFirst_name());
        verification.setLastName(data.getCustomer().getLast_name());
        verification.setEmail(data.getCustomer().getEmail());
        verification.setPhoneNumber(data.getCustomer().getPhone());
        verification.setVerified(true);
        verification.setStatus(data.getStatus());
        verification.setGatewayResponse(data.getGateway_response());
        verification.setAmount(data.getAmount() / 100);
        verification.setPaidAt(data.getPaid_at());
        verification.setChannel(data.getChannel());
        verification.setCurrency(data.getCurrency());
        verification.setIpAddress(data.getIp_address());
        verification.setCreatedAt(LocalDateTime.now());
        transactionVerificationRepository.save(verification);

        // Update related transaction and booking
        transactionRepository.findByReference(reference).ifPresent(tx -> {
            userRepository.findByEmail(tx.getEmail()).ifPresent(user -> {
                bookingRepository.findByBookingCode(tx.getBookingCode()).ifPresent(booking -> {
                    if (booking.getUser().getUserCode().equals(user.getUserCode())) {
                        tx.setPaystackPaymentStatus(PaystackPaymentStatus.SUCCESS);
                        booking.setPaymentStatus(PaymentStatus.PAID);
                    } else {
                        tx.setPaystackPaymentStatus(PaystackPaymentStatus.FAILED);
                        booking.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                    transactionRepository.save(tx);
                    bookingRepository.save(booking);
                });
            });
        });

        return new ResponseModel(HttpStatus.OK.value(), "Transaction verified successfully", verificationResponse);

    }

    @Override
    public ResponseModel listTransactions(int perPage, String nextPage) {
        String url = paystackBaseUrl + "/transaction";

        if (nextPage != null) {
            url += "?perPage=" + perPage + "&page=" + nextPage;
        } else {
            url += "?perPage=" + perPage;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(secretKey.trim());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<PaystackTransactionListResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PaystackTransactionListResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                PaystackTransactionListResponse transactionListResponse = response.getBody();

                if (transactionListResponse != null && transactionListResponse.isStatus()) {
                    return new ResponseModel(HttpStatus.OK.value(), "Transactions retrieved", transactionListResponse);
                } else {
                    return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Failed to retrieve transactions", null);
                }
            } else {
                return new ResponseModel(response.getStatusCode().value(), "Failed to retrieve transactions", "invalid key");
            }
        }catch(GlobalRequestException e){
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve transactions", e.getMessage());
        } catch (Exception e) {
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve transactions", e.getMessage());
        }
    }

    @Override
    public ResponseModel fetchTransaction(String reference) {
        String url;
        Optional<TransactionVerification> existingTransaction = transactionVerificationRepository.findByTransactionReference(reference);
        if (existingTransaction.isPresent()) {
            url = paystackBaseUrl + "/transaction/" + existingTransaction.get().getTransactionId();
        } else {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Transaction not found", null);
        }
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(secretKey.trim());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Fetch transaction from Paystack API
            ResponseEntity<PaystackTransactionFetchResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, PaystackTransactionFetchResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                PaystackTransactionFetchResponse transactionFetchResponse = response.getBody();

                if (transactionFetchResponse != null && transactionFetchResponse.isStatus()) {
                    return new ResponseModel(HttpStatus.OK.value(), "Transaction retrieved", transactionFetchResponse);
                } else {
                    return new ResponseModel(HttpStatus.BAD_REQUEST.value(), "Failed to retrieve transaction", null);
                }
            } else {
                return new ResponseModel(response.getStatusCode().value(), "Failed to retrieve transaction", "invalid key");
            }
        }catch(GlobalRequestException e){
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve transactions", e.getMessage());
        } catch (Exception e) {
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to retrieve transactions", e.getMessage());
        }
    }

}
