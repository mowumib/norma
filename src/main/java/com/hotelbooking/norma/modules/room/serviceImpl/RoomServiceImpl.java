package com.hotelbooking.norma.modules.room.serviceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.response.RoomResponse;
import com.hotelbooking.norma.entity.Room;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.room.repository.RoomRepository;
import com.hotelbooking.norma.modules.room.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;

    @Override
    public ResponseModel getRoomByRoomCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow(
        () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Room"), roomResponse);
    }

}










