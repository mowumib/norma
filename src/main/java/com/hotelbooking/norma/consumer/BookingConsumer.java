// package com.hotelbooking.norma.consumer;

// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.hotelbooking.norma.config.RabbitMQConfig;
// import com.hotelbooking.norma.dto.BookingDto;
// import com.hotelbooking.norma.dto.Request.BookingMessage;
// import com.hotelbooking.norma.dto.Request.PaymentMessage;
// import com.hotelbooking.norma.entity.Booking;
// import com.hotelbooking.norma.entity.Hotel;
// import com.hotelbooking.norma.entity.Room;
// import com.hotelbooking.norma.entity.User;
// import com.hotelbooking.norma.enums.BookingStatus;
// import com.hotelbooking.norma.enums.PaymentStatus;
// import com.hotelbooking.norma.enums.RoomStatus;
// import com.hotelbooking.norma.enums.RoomType;
// import com.hotelbooking.norma.repository.BookingRepository;
// import com.hotelbooking.norma.repository.HotelRepository;
// import com.hotelbooking.norma.repository.RoomRepository;
// import com.hotelbooking.norma.repository.UserRepository;

// import java.time.temporal.ChronoUnit;
// import java.util.Optional;
// import java.util.UUID;

// @Component
// public class BookingConsumer {

//     @Autowired
//     private HotelRepository hotelRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private RoomRepository roomRepository;

//     @Autowired
//     private BookingRepository bookingRepository;

//     @Autowired
//     private RabbitTemplate rabbitTemplate;

//     @RabbitListener(queues = RabbitMQConfig.BOOKING_QUEUE)
//     public void processBookingRequest(BookingMessage message) {
//         System.out.println("Processing booking request for user: " + message.getUserCode());

//         try {
//             // Retrieve entities using data from the message
//             Hotel hotel = hotelRepository.findByHotelCodeWithRooms(message.getHotelCode())
//                 .orElseThrow(() -> new IllegalArgumentException("Hotel not found.")); // Throw a specific exception

//             User user = userRepository.findByUserCode(message.getUserCode()).get();
//             BookingDto dto = message.getDto();

//             RoomType requestedRoomType = dto.getRoomType();
//             Optional<Room> availableRoom = hotel.getRooms().stream()
//                     .filter(room -> room != null && room.getRoomStatus().equals(RoomStatus.AVAILABLE) && room.getRoomType() == requestedRoomType)
//                     .findFirst();

//             if (availableRoom.isPresent()) {
//                 Room selectedRoom = availableRoom.get();

//                 long numberOfNights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
//                 if (numberOfNights < 1) {
//                     System.out.println("Booking failed: Must be for at least one night.");
//                     return; // Stop processing this message
//                 }

//                 int totalAmount = (int) (selectedRoom.getRoomPrice() * numberOfNights);
//                 Booking booking = new Booking();
//                 booking.setRoom(selectedRoom);
//                 booking.setRoomType(requestedRoomType);
//                 booking.setBookingCode("BOOKING-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
//                 booking.setAmount(totalAmount);
//                 booking.setPaymentStatus(PaymentStatus.UNPAID);
//                 booking.setCheckInDate(dto.getCheckInDate());
//                 booking.setCheckOutDate(dto.getCheckOutDate());
//                 booking.setBookingStatus(BookingStatus.PENDING);
//                 booking.setHotel(hotel);
//                 booking.setUser(user);
                
//                 selectedRoom.setRoomStatus(RoomStatus.BOOKED);

//                 // Save both the booking and the updated room status
//                 Booking savedBooking = bookingRepository.save(booking);
//                 roomRepository.save(selectedRoom);

//                 PaymentMessage paymentMessage = new PaymentMessage(
//                     savedBooking.getBookingCode(),
//                     user.getEmail(),
//                     savedBooking.getAmount()
//                 );
//                 rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_QUEUE, paymentMessage);
                
//                 System.out.println("Booking created and payment initiated for: " + savedBooking.getBookingCode());
        
                
                
//             } else {
//                 System.out.println("Booking failed: No available rooms for user " + user.getUserCode());
//             }

//         } catch (Exception e) {
//             System.err.println("An error occurred while processing booking request: " + e.getMessage());
//         }
//     }
// }