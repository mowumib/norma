package com.hotelbooking.norma.modules.admin.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.request.HotelDto;
import com.hotelbooking.norma.dto.request.UpdateHotelRequest;
import com.hotelbooking.norma.modules.admin.service.AdminHotelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/hotels")
@RequiredArgsConstructor
@Validated
@Tag(name = "ADMIN HOTEL CONTROLLER REST APIS IN HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class AdminHotelController {

    private final AdminHotelService adminHotelService;
    @Operation(
        summary = "Add hotel",
        description = "Hotel APIs"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add-hotel", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> addHotel(@ModelAttribute  @Valid HotelDto dto) throws IOException, SQLException{
        ResponseModel responseModel = adminHotelService.addHotel( dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete hotel by code",
        description = "REST API for deleting a hotel"
    )
    @DeleteMapping("")
    public ResponseEntity<ResponseModel> deleteHotelByHotelCode(@RequestParam String hotelCode) {
        ResponseModel responseModel = adminHotelService.deleteHotelByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get hotel by code",
        description = "REST API for getting a hotel"
    )
    @GetMapping("/hotel-by-code/{hotelCode}")
    public ResponseEntity<ResponseModel> getHotelByHotelCode(@PathVariable String hotelCode) {
        ResponseModel responseModel = adminHotelService.getHotelByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all hotels",
        description = "REST API for getting all hotels"
    )
    @GetMapping("")
    public ResponseEntity<ResponseModel> getAllHotels() {
        return ResponseEntity.ok(adminHotelService.getAllHotels());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update hotel by code",
        description = "REST API for updating a hotel"
    )
    @PostMapping(value = "/update-hotel", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel> updateHotel(@RequestParam String hotelCode, @ModelAttribute UpdateHotelRequest dto) {
        ResponseModel responseModel = adminHotelService.updateHotelByHotelCode(hotelCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
