package com.hotelbooking.norma.modules.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotelbooking.norma.entity.Room;

public interface AdminRoomRepository extends JpaRepository<Room, Long>{

    Optional<Room> findByRoomCode(String roomCode);

}
