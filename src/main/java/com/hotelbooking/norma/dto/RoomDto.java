package com.hotelbooking.norma.dto;

import org.springframework.web.multipart.MultipartFile;

import com.hotelbooking.norma.enums.RoomType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomDto {
    @NotNull(message = "Room type cannot be empty")
    private RoomType roomType = RoomType.STANDARD;

    @NotEmpty(message = "Room number cannot be empty")
    private String roomNumber;

    @NotNull(message = "Room price cannot be empty")
    private Integer roomPrice;

    @NotEmpty(message = "Room description cannot be empty")
    private String roomDescription;

    @NotNull(message = "Photo cannot be null")
    private MultipartFile photo;

}
