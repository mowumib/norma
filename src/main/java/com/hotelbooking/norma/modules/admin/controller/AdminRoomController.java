package com.hotelbooking.norma.modules.admin.controller;

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
import com.hotelbooking.norma.dto.request.RoomDto;
import com.hotelbooking.norma.dto.request.UpdateRoomRequest;
import com.hotelbooking.norma.modules.admin.service.AdminRoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/rooms")
@RequiredArgsConstructor
@Validated
@Tag(name = "ADMIN ROOMS CONTROLLER REST APIS IN NORMA HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class AdminRoomController {

    private final AdminRoomService adminRoomService;

    // @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add room by hotel code")
    @PostMapping(value = "/add-room", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> addRoom(@RequestParam String hotelCode, @ModelAttribute @Valid RoomDto dto) 
    throws SQLException, IOException{
        ResponseModel responseModel = adminRoomService.addRoom(hotelCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get room by code")
    @GetMapping("/room")
    public ResponseEntity<ResponseModel> getRoomByRoomCode(@RequestParam String roomCode) {
        ResponseModel responseModel = adminRoomService.getRoomByRoomCode(roomCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all available rooms by hotel code")
    @GetMapping("/available-rooms")
    public ResponseEntity<ResponseModel> getAllAvailableRoom(@RequestParam String hotelCode) {
        ResponseModel responseModel = adminRoomService.getAllAvailableRoom(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all booked rooms by hotel code")
    @GetMapping("/booked-rooms")
    public ResponseEntity<ResponseModel> getAllBookedRoom(@RequestParam String hotelCode) {
        ResponseModel responseModel = adminRoomService.getAllBookedRoom(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all rooms")
    @GetMapping("")
    public ResponseEntity<ResponseModel> getAllRooms(@RequestParam String hotelCode) {
        ResponseModel responseModel = adminRoomService.getAllRooms(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room by room code")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel> deleteRoomByRoomCode(@RequestParam String roomCode) {
        ResponseModel responseModel = adminRoomService.deleteRoomByRoomCode(roomCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room by room code")
    @PostMapping(value = "/update-room", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> updateRoomByRoomCode(@RequestParam String roomCode, @ModelAttribute UpdateRoomRequest dto) {
        ResponseModel responseModel = adminRoomService.updateRoomByRoomCode(roomCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

}
