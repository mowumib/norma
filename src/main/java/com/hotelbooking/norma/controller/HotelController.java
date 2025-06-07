package com.hotelbooking.norma.controller;


import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.HotelDto;
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.UpdateHotelRequest;
import com.hotelbooking.norma.service.HotelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "HOTEL CONTROLLER REST APIS IN HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class HotelController {

    private final HotelService hotelService;

    
    @Operation(
        summary = "Add hotel",
        description = "Hotel APIs"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add-hotel", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> addHotel(@ModelAttribute  @Valid HotelDto dto) throws IOException, SQLException{
        ResponseModel responseModel = hotelService.addHotel( dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN' ) or hasRole('USER')")
    @Operation(
        summary = "Get hotel by code",
        description = "REST API for getting a hotel"
    )
    @GetMapping("/hotel-by-code")
    public ResponseEntity<ResponseModel> getHotelByHotelCode(@RequestParam String hotelCode) {
        ResponseModel responseModel = hotelService.getHotelByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN' ) or hasRole('USER')")
    @Operation(
        summary = "Get all hotels",
        description = "REST API for getting all hotels"
    )
    @GetMapping("/all-hotels")
    public ResponseEntity<ResponseModel> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete hotel by code",
        description = "REST API for deleting a hotel"
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseModel> deleteHotelByHotelCode(@RequestParam String hotelCode) {
        ResponseModel responseModel = hotelService.deleteHotelByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Get hotel by location",
        description = "REST API for getting all hotels by location"
    )
    @GetMapping("/hotel-by-location")
    public ResponseEntity<ResponseModel> getHotelByLocation(@RequestParam String location) {
        ResponseModel responseModel = hotelService.getHotelByLocation(location);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Get hotel by name",
        description = "REST API for getting hotel by name"
    )
    @GetMapping("/hotel-by-name")
    public ResponseEntity<ResponseModel> getHotelByName(@RequestParam String name) {
        ResponseModel responseModel = hotelService.getHotelByName(name);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update hotel by code",
        description = "REST API for updating a hotel"
    )
    @PostMapping(value = "/update-hotel", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> updateHotel(@RequestParam String hotelCode, @ModelAttribute UpdateHotelRequest dto) {
        ResponseModel responseModel = hotelService.updateHotelByHotelCode(hotelCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
