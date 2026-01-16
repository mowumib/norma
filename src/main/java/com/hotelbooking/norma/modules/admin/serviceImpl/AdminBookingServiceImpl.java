package com.hotelbooking.norma.modules.admin.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.entity.Booking;
import com.hotelbooking.norma.entity.User;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.admin.repository.AdminBookingRepository;
import com.hotelbooking.norma.modules.admin.service.AdminBookingService;
import com.hotelbooking.norma.modules.onboarding.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final AdminBookingRepository bookingRepository;
    private final UserRepository userRepository;
    @Override
    public ResponseModel getAllBookingsByHotelCode(String hotelCode) {
        List<Booking> bookings = bookingRepository.findByHotel_HotelCode(hotelCode);
        if (bookings.isEmpty()) {
            return new ResponseModel(HttpStatus.NOT_FOUND.value(), String.format(Message.NOT_FOUND, "Hotel"), null);
        }
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Bookings"), bookings);
    }

    @Override
    public ResponseModel getAllBookingsByUserCode(String userCode){
    User user = userRepository.findByUserCode(userCode).orElseThrow(
        () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "User"), HttpStatus.NOT_FOUND));;
    
        List<Booking> bookings = bookingRepository.findByUser_UserCode(user.getUserCode());
    
        return new ResponseModel(HttpStatus.OK.value(), 
        String.format(Message.SUCCESS_GET, "Bookings"), bookings);
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
    public ResponseModel getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

            return new ResponseModel(HttpStatus.OK.value(), 
            String.format(Message.SUCCESS_GET, "Booking"), booking);
    }
}
