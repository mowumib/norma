package com.hotelbooking.norma.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelbooking.norma.entity.Booking;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    public List<Booking> findByUser_UserCode(String userCode);
    public List<Booking> findByHotel_HotelCode(String hotelCode);
    public Optional<Booking> findByBookingCode(String bookingCode);

}
