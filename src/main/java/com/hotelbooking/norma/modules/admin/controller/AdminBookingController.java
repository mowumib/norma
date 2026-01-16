package com.hotelbooking.norma.modules.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.modules.admin.service.AdminBookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
@Validated
@Tag(name = "ADMIN BOOKING CONTROLLER REST APIS IN HOTEL SERVICE", description = "REST APIS IN HOTEL SERVICE")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;
    
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all booking",
            description = "REST API for getting all booking"
    )
    @GetMapping("/all-bookings")
    public ResponseEntity<ResponseModel> getAllBooking() {
        ResponseModel responseModel = adminBookingService.getAllBooking();
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all bookings by hotel code",
        description = "REST API for getting all bookings by hotel code"
    )
    @GetMapping("/all-bookings-by-hotel-code/{hotelCode}")
    public ResponseEntity<ResponseModel> getAllBookingsByHotelCode(@PathVariable String hotelCode){
        ResponseModel responseModel = adminBookingService.getAllBookingsByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get booking by booking code",
        description = "REST API for getting booking by code"
    )
    @GetMapping("/booking/{bookingCode}")
    public ResponseEntity<ResponseModel> getBookingByCode(@PathVariable String bookingCode){
        ResponseModel responseModel = adminBookingService.getBookingByCode(bookingCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);

    }
}
