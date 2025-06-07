package com.hotelbooking.norma.service;

import com.hotelbooking.norma.dto.BookingDto;
import com.hotelbooking.norma.dto.ResponseModel;

public interface BookingService {
    public ResponseModel bookRoom(String hotelCode, String userCode, BookingDto dto);

    public ResponseModel cancelBooking(String bookingCode);

    public ResponseModel getAllBookingsByUserCode(String userCode);

    public ResponseModel getBookingByCode(String bookingCode);

    public ResponseModel getAllBookingsByHotelCode(String hotelCode);

    public ResponseModel completeBooking(String bookingCode);

    public ResponseModel getAllBooking();

}
