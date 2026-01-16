package com.hotelbooking.norma.modules.room.service;

import com.hotelbooking.norma.dto.ResponseModel;

public interface RoomService {

    public ResponseModel getRoomByRoomCode(String roomCode);

}
