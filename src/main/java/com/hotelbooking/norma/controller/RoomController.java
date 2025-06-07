package com.hotelbooking.norma.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.RoomDto;
import com.hotelbooking.norma.dto.Request.UpdateRoomRequest;
import com.hotelbooking.norma.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Validated
@Tag(name = "ROOM CONTROLLER REST APIS IN HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class RoomController {

    private final RoomService roomService;
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Add room by hotel code",
        description = "REST API for adding a room"
    )
    @PostMapping(value = "/add-room", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> addRoom(@RequestParam String hotelCode, @ModelAttribute @Valid RoomDto dto) 
    throws SQLException, IOException{
        ResponseModel responseModel = roomService.addRoom(hotelCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    @Operation(
        summary = "Get room by code",
        description = "REST API for getting a room"
    )
    @GetMapping("/room")
    public ResponseEntity<ResponseModel> getRoomByRoomCode(@RequestParam String roomCode) {
        ResponseModel responseModel = roomService.getRoomByRoomCode(roomCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all available rooms by hotel code",
        description = "REST API for getting all available rooms"
    )
    @GetMapping("/all-available-rooms")
    public ResponseEntity<ResponseModel> getAllAvailableRoom(@RequestParam String hotelCode) {
        ResponseModel responseModel = roomService.getAllAvailableRoom(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all booked rooms by hotel code",
        description = "REST API for getting all booked room"
    )
    @GetMapping("/all-booked-rooms")
    public ResponseEntity<ResponseModel> getAllBookedRoom(@RequestParam String hotelCode) {
        ResponseModel responseModel = roomService.getAllBookedRoom(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all rooms",
        description = "REST API for getting all rooms"
    )
    @GetMapping("/all-rooms")
    public ResponseEntity<ResponseModel> getAllRooms(@RequestParam String hotelCode) {
        ResponseModel responseModel = roomService.getAllRooms(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete room by room code",
        description = "REST API for deleting room"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel> deleteRoomByRoomCode(@RequestParam String roomCode) {
        ResponseModel responseModel = roomService.deleteRoomByRoomCode(roomCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update room by room code",
        description = "REST API for updating room"
    )
    @PostMapping(value = "/update-room", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> updateRoomByRoomCode(@RequestParam String roomCode, @ModelAttribute UpdateRoomRequest dto) {
        ResponseModel responseModel = roomService.updateRoomByRoomCode(roomCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
