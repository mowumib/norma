package com.hotelbooking.norma.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelDto {
    @NotEmpty(message = "Hotel name cannot be empty")
    private String name;

    @NotEmpty(message = "Hotel location cannot be empty")
    private String location;

    @NotNull(message = "Photo cannot be null")
    private MultipartFile photo;
}
