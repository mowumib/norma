package com.hotelbooking.norma.modules.admin.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.entity.Booking;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.admin.service.AdminBookingService;
import com.hotelbooking.norma.modules.booking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository bookingRepository;
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
    public ResponseModel getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Booking"), HttpStatus.NOT_FOUND));

            return new ResponseModel(HttpStatus.OK.value(), 
            String.format(Message.SUCCESS_GET, "Booking"), booking);
    }
}
