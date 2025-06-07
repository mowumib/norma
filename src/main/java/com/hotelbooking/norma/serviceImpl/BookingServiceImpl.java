package com.hotelbooking.norma.serviceImpl;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
import com.hotelbooking.norma.enums.RoomType;
import com.hotelbooking.norma.enums.Status;
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
        List<Room> rooms = hotel.getRooms();
        
        List<Room> availableRooms = new ArrayList<>();
        for(Room room: rooms) {
            if(room == null) {
                continue;
            }
            if(room.getStatus().equals(Status.AVAILABLE) && room.getRoomType() == requestedRoomType) {
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            throw new GlobalRequestException("No available rooms in this hotel.", HttpStatus.BAD_REQUEST);
        }

        Room selectedRoom = availableRooms.get(0);    

        long numberOfNights = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (numberOfNights < 1) {
            throw new GlobalRequestException("Booking must be for at least one night.", HttpStatus.BAD_REQUEST);
        }

        int totalAmount = (int) (selectedRoom.getRoomPrice() * numberOfNights);
        Booking booking = new Booking();
        booking.setRoom(selectedRoom);
        booking.setBookingCode("BOOKING-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        booking.setAmount(totalAmount);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setBookingStatus(BookingStatus.BOOKED);

        booking.setHotel(hotel);
        booking.setUser(user);
        booking.setRoomType(selectedRoom.getRoomType());
        selectedRoom.setStatus(Status.BOOKED);

        bookingRepository.save(booking);
        roomRepository.save(selectedRoom);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_BOOKED, "Room"), booking);

    }

    @Override
    public ResponseModel cancelBooking(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
            .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

        Room room = roomRepository.findByRoomCode(booking.getRoom().getRoomCode())
            .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        if (room.getStatus().equals(Status.AVAILABLE)) {
            return new ResponseModel(HttpStatus.OK.value(), String.format(Message.ALREADY_AVAILABLE, "Room"));
        }

        room.setStatus(Status.AVAILABLE);
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

    @Override
    public ResponseModel completeBooking(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

        String userCode = booking.getUser().getUserCode();

        User user = userRepository.findByUserCode(userCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));

        PaystackPaymentDto paystackPaymentDto = new PaystackPaymentDto();
        paystackPaymentDto.setAmount(booking.getAmount());
        paystackPaymentDto.setEmail(user.getEmail());
        paystackPaymentDto.setBookingCode(booking.getBookingCode());
        paystackPaymentDto.setUserCode(user.getUserCode());
        

        ResponseModel response = paystackService.initializeTransaction(paystackPaymentDto);

        if (response.getStatusCode() >= 500) {
            return new ResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getMessage(), null);
        } else if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), response.getMessage(), response.getData());
        }

        return new ResponseModel(HttpStatus.OK.value(), response.getMessage(), response.getData());
    }

}
