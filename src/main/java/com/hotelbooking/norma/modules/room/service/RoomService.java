package com.hotelbooking.norma.modules.room.service;

import java.io.IOException;
import java.sql.SQLException;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.request.RoomDto;
import com.hotelbooking.norma.dto.request.UpdateRoomRequest;

public interface RoomService {

    public ResponseModel addRoom(String hotelCode, RoomDto dto) throws IOException, SQLException;

    public ResponseModel getRoomByRoomCode(String roomCode);

    public ResponseModel getAllAvailableRoom(String hotelCode);

    public ResponseModel getAllBookedRoom(String hotelCode);

    public ResponseModel getAllRooms(String hotelCode);

    public ResponseModel deleteRoomByRoomCode(String roomCode);

    public ResponseModel updateRoomByRoomCode(String roomCode, UpdateRoomRequest dto);

}
