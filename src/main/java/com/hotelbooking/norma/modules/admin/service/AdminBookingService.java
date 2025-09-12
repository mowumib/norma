package com.hotelbooking.norma.modules.admin.service;

import com.hotelbooking.norma.dto.ResponseModel;

public interface AdminBookingService {
    public ResponseModel getAllBooking(); //add filters, and pagination

    public ResponseModel getBookingByCode(String bookingCode);

    public ResponseModel getAllBookingsByHotelCode(String hotelCode);
}
