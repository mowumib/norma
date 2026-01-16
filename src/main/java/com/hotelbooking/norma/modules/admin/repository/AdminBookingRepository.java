package com.hotelbooking.norma.modules.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.entity.Booking;

public interface AdminBookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByUser_UserCode(String userCode);
    public List<Booking> findByHotel_HotelCode(String hotelCode);
    public Optional<Booking> findByBookingCode(String bookingCode);
}
