package com.hotelbooking.norma.modules.booking.service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.request.BookingDto;

public interface BookingService {
    public ResponseModel bookRoom(String hotelCode, String userCode, BookingDto dto);

    public ResponseModel cancelBooking(String bookingCode);

    public ResponseModel getAllBookingsByUserCode(String userCode);

    public ResponseModel getBookingByCode(String bookingCode);

    public ResponseModel getAllBookingsByHotelCode(String hotelCode);

    public ResponseModel getAllBooking();

}
