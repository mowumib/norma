package com.hotelbooking.norma.serviceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.BookingDto;
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.entity.Booking;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.entity.Room;
import com.hotelbooking.norma.entity.User;
import com.hotelbooking.norma.enums.BookingStatus;
import com.hotelbooking.norma.enums.PaymentStatus;
import com.hotelbooking.norma.enums.RoomStatus;
import com.hotelbooking.norma.enums.RoomType;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentDto;
import com.hotelbooking.norma.paystack.service.PaystackService;
import com.hotelbooking.norma.repository.BookingRepository;
import com.hotelbooking.norma.repository.HotelRepository;
import com.hotelbooking.norma.repository.RoomRepository;
import com.hotelbooking.norma.repository.UserRepository;
import com.hotelbooking.norma.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;

    private final PaystackService paystackService;
    
    @Override
    public ResponseModel bookRoom(String hotelCode, String userCode, BookingDto dto) {

        Hotel hotel = hotelRepository.findByHotelCode(hotelCode).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        User user = userRepository.findByUserCode(userCode).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));
        
        // Filter available rooms
        RoomType requestedRoomType = dto.getRoomType();
        Optional<Room> availableRoom = hotel.getRooms().stream()
        .filter(room -> room != null && room.getRoomStatus().equals(RoomStatus.AVAILABLE) && room.getRoomType() == requestedRoomType)
        .findFirst();
        
        if (!availableRoom.isPresent()) {
            throw new GlobalRequestException("No available rooms in this hotel.", HttpStatus.BAD_REQUEST);
        }

        Room selectedRoom = availableRoom.get();    

        long numberOfNights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (numberOfNights < 1) {
            throw new GlobalRequestException("Booking must be for at least one night.", HttpStatus.BAD_REQUEST);
        }

        int totalAmount = (int) (selectedRoom.getRoomPrice() * numberOfNights);

        Booking booking = new Booking();
        booking.setRoom(selectedRoom);
        booking.setRoomType(requestedRoomType);
        booking.setBookingCode("BOOKING-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        booking.setAmount(totalAmount);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setHotel(hotel);
        booking.setUser(user);
        booking.setRoomType(selectedRoom.getRoomType());
        selectedRoom.setRoomStatus(RoomStatus.BOOKED);

        Booking savedBooking = bookingRepository.save(booking);
        roomRepository.save(selectedRoom);


        PaystackPaymentDto paystackPaymentDto = new PaystackPaymentDto();
        paystackPaymentDto.setAmount(totalAmount);
        paystackPaymentDto.setEmail(user.getEmail());
        paystackPaymentDto.setBookingCode(savedBooking.getBookingCode());
        paystackPaymentDto.setUserCode(user.getUserCode());

        ResponseModel response = paystackService.initializeTransaction(paystackPaymentDto);

        // Return the response, which includes the authorizationUrl
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            System.out.println("Booking created and payment initialized successfully.");
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_BOOKED, "Room"), response);
        } else {
            // Handle payment initialization failure
            System.err.println("Payment initialization failed. Booking reverted.");
            // Revert the booking and room status to avoid a stranded booking
            booking.setBookingStatus(BookingStatus.FAILED);
            selectedRoom.setRoomStatus(RoomStatus.AVAILABLE);
            bookingRepository.save(booking);
            roomRepository.save(selectedRoom);
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.FAILED_BOOKED, "Room"), response);
        }

        // return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_BOOKED, "Room"), savedBooking);

    }

    /* @Override
    public ResponseModel bookRoom(String hotelCode, String userCode, BookingDto dto) {
        hotelRepository.findByHotelCode(hotelCode)
                .orElseThrow(() -> new GlobalRequestException(String.format("Hotel not found with code %s", hotelCode), HttpStatus.NOT_FOUND));

        userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new GlobalRequestException(String.format("User not found with code %s", userCode), HttpStatus.NOT_FOUND));

        //Create a message payload with the booking details
        BookingMessage bookingMessage = new BookingMessage(hotelCode, userCode, dto);

        // Step 3: Send the message to the queue.
        // The queue name 'booking_requests_queue' is defined in our RabbitMQ config.
        rabbitTemplate.convertAndSend("booking_requests_queue", bookingMessage);

        // Step 4: Return an immediate, non-blocking response to the user.
        return new ResponseModel(HttpStatus.ACCEPTED.value(), "Your booking request is being processed. You will be notified shortly.", null);
    } */

    @Override
    public ResponseModel cancelBooking(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
            .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

        Room room = roomRepository.findByRoomCode(booking.getRoom().getRoomCode())
            .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        if (room.getRoomStatus().equals(RoomStatus.AVAILABLE)) {
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.ALREADY_AVAILABLE, "Room"));
        }

        room.setRoomStatus(RoomStatus.AVAILABLE);
        booking.setBookingStatus(BookingStatus.CANCELLED);

        roomRepository.save(room);
        bookingRepository.delete(booking);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_CANCELLED, "Booking"), booking);

    }
    
    @Override
    public ResponseModel getAllBookingsByUserCode(String userCode) {
    User user = userRepository.findByUserCode(userCode).orElseThrow(
        () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));;
    
        List<Booking> bookings = bookingRepository.findByUser_UserCode(user.getUserCode());
    
        return new ResponseModel(HttpStatus.OK.value(), 
            String.format(Message.SUCCESS_GET, "Bookings"), bookings);
    }

    @Override
    public ResponseModel getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

            return new ResponseModel(HttpStatus.OK.value(), 
            String.format(Message.SUCCESS_GET, "Booking"), booking);
    }

    @Override
    public ResponseModel getAllBookingsByHotelCode(String hotelCode) {
        List<Booking> bookings = bookingRepository.findByHotel_HotelCode(hotelCode);
        if (bookings.isEmpty()) {
            return new ResponseModel(HttpStatus.NOT_FOUND.value(), String.format(Message.NOT_FOUND, "Hotel"), null);
        }
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Bookings"), bookings);
    }

    @Override
    public ResponseModel getAllBooking() {

        List<Booking> bookings = bookingRepository.findAll();
        List<String> roomCodes = new ArrayList<>();
        for(Booking booking: bookings){
            roomCodes.add(booking.getRoom().getRoomCode());
        }
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Booking"), bookings);
    }

}
