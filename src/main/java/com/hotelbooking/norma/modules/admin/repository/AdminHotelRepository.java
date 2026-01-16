package com.hotelbooking.norma.modules.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.entity.Hotel;

public interface AdminHotelRepository extends JpaRepository<Hotel, Long> {

    public Optional<Hotel> findByHotelCode(String hotelCode);

}
