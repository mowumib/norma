package com.hotelbooking.norma.modules.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotelbooking.norma.entity.Booking;
import com.hotelbooking.norma.enums.BookingStatus;
import com.hotelbooking.norma.enums.PaymentStatus;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    public List<Booking> findByUser_UserCode(String userCode);
    public List<Booking> findByHotel_HotelCode(String hotelCode);
    public Optional<Booking> findByBookingCode(String bookingCode);
    public List<Booking> findAllByBookingStatusAndPaymentStatusAndExpiryTimeBefore(BookingStatus pending, PaymentStatus unpaid,
        LocalDateTime now);

}
