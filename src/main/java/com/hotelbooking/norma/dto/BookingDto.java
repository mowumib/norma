package com.hotelbooking.norma.dto;

import java.time.LocalDate;

import com.hotelbooking.norma.enums.RoomType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingDto {

    @NotNull(message = "Check-in date cannot be empty")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date cannot be empty")
    private LocalDate checkOutDate;

    @NotNull(message = "Room type cannot be empty")
    private RoomType roomType;
}
