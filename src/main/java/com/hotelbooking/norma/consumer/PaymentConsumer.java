// package com.hotelbooking.norma.consumer;

// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;

// import com.hotelbooking.norma.config.RabbitMQConfig;
// import com.hotelbooking.norma.dto.ResponseModel;
// import com.hotelbooking.norma.dto.Request.PaymentMessage;
// import com.hotelbooking.norma.entity.Booking;
// import com.hotelbooking.norma.enums.BookingStatus;
// import com.hotelbooking.norma.paystack.dto.PaystackPaymentDto;
// import com.hotelbooking.norma.paystack.service.PaystackService;
// import com.hotelbooking.norma.repository.BookingRepository;

// @Component
// public class PaymentConsumer {

//     @Autowired
//     private BookingRepository bookingRepository;

//     @Autowired
//     private PaystackService paystackService;

//     @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
//     public void processPaymentRequest(PaymentMessage message) {
//         System.out.println("Processing payment for booking code: " + message.getBookingCode());

//         try {
//             // Find the booking from the database using the code from the message.
//             Booking booking = bookingRepository.findByBookingCode(message.getBookingCode())
//                 .orElseThrow(() -> new RuntimeException("Booking not found for payment."));

//             PaystackPaymentDto paystackPaymentDto = new PaystackPaymentDto();
//             paystackPaymentDto.setAmount(message.getAmount());
//             paystackPaymentDto.setEmail(message.getEmail());
//             paystackPaymentDto.setBookingCode(message.getBookingCode());
//             paystackPaymentDto.setUserCode(booking.getUser().getUserCode());

//             ResponseModel response = paystackService.initializeTransaction(paystackPaymentDto);

//             // Update booking status based on the payment service response
//             if (response.getStatusCode() == HttpStatus.OK.value()) {
//                 System.out.println("Payment initialized successfully for booking " + booking.getBookingCode());
//                 booking.setBookingStatus(BookingStatus.PENDING);
//             } else {
//                 System.err.println("Payment initialization failed for booking " + booking.getBookingCode());
//                 booking.setBookingStatus(BookingStatus.FAILED);
//             }
//             bookingRepository.save(booking);

//         } catch (Exception e) {
//             System.err.println("Error processing payment request: " + e.getMessage());
//         }
//     }
// }