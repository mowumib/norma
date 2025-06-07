package com.hotelbooking.norma.dto.Request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name =" Update Hotel Request", description = "Request to update a hotel")
public class UpdateHotelRequest {
    
    @Schema(description = "Name of the hotel", nullable = true)
    private String name;

    @Schema(description = "Location of the hotel", nullable = true)
    private String location;

    @Schema(description = "Photo of the hotel")
    private MultipartFile photo;

    @Schema(description = "Whether to clear the photo in the google drive or not", nullable = false)
    private Boolean clearPhoto = false;
}
