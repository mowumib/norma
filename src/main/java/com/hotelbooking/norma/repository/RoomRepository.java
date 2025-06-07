package com.hotelbooking.norma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.entity.Room;


public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    // Optional<Room> findByHotelCode(String hotelCode);
}
