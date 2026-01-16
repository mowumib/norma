package com.hotelbooking.norma.modules.room.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.modules.room.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Validated
@Tag(name = "ROOM CONTROLLER REST APIS IN NORMA HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class RoomController {

    private final RoomService roomService;
    
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get room by code")
    @GetMapping("/room")
    public ResponseEntity<ResponseModel> getRoomByRoomCode(@RequestParam String roomCode) {
        ResponseModel responseModel = roomService.getRoomByRoomCode(roomCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

}
