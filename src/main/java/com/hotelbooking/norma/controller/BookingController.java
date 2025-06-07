package com.hotelbooking.norma.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.BookingDto;
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/bookings")
@Tag(name = "Booking Controller REST API IN USER SERVICE", description = "Booking APIs")
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Add booking",
        description = "REST API for adding booking"
    )
    @PostMapping("/add-booking")
    public ResponseEntity<ResponseModel> addBooking(@RequestParam String hotelCode, @RequestParam String userCode, @RequestBody BookingDto dto) {
        ResponseModel responseModel = bookingService.bookRoom(hotelCode, userCode, dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Get booking by booking code",
        description = "REST API for getting booking by code"
    )
    @GetMapping("/booking")
    public ResponseEntity<ResponseModel> getBookingByCode(@RequestParam String bookingCode){
        ResponseModel responseModel = bookingService.getBookingByCode(bookingCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all booking",
            description = "REST API for getting all booking"
    )
    @GetMapping("/all-bookings")
    public ResponseEntity<ResponseModel> getAllBooking() {
        ResponseModel responseModel = bookingService.getAllBooking();
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Cancel Booking",
        description = "REST API for canceling booking"
    )
    @DeleteMapping("/cancel-booking")
    public ResponseEntity<ResponseModel> cancelBooking(@RequestParam String bookingCode){
        ResponseModel responseModel = bookingService.cancelBooking(bookingCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Get all bookings by user code",
        description = "REST API for getting all bookings"
    )
    @GetMapping("/all-bookings-by-user-code")
    public ResponseEntity<ResponseModel> getAllBookingsByUserCode(@RequestParam String userCode){
        ResponseModel responseModel = bookingService.getAllBookingsByUserCode(userCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Get all bookings by hotel code",
        description = "REST API for getting all bookings by hotel code"
    )
    @GetMapping("/all-bookings-by-hotel-code")
    public ResponseEntity<ResponseModel> getAllBookingsByHotelCode(@RequestParam String hotelCode){
        ResponseModel responseModel = bookingService.getAllBookingsByHotelCode(hotelCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(
        summary = "Complete booking",
        description = "REST API for complete"
    )
    @PostMapping("/complete-booking")
    public ResponseEntity<ResponseModel> completeBooking(@RequestParam String bookingCode){
        ResponseModel responseModel = bookingService.completeBooking(bookingCode);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }
}
