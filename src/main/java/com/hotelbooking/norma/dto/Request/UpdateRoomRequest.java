package com.hotelbooking.norma.dto.Request;

import org.springframework.web.multipart.MultipartFile;

import com.hotelbooking.norma.enums.RoomType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "Update Room Request", description = "Request to update a room")
public class UpdateRoomRequest {

    @Schema(description = "Room number")
    private String roomNumber;

    @Schema(description = "Room type")
    private RoomType roomType;

    @Schema(description = "Room price")
    private Integer roomPrice;

    @Schema(description = "Room description")
    private String roomDescription;

    @Schema(description = "Room photo")
    private MultipartFile photo;

    @Schema(description = "Whether to clear the photo in the google drive or not")
    private Boolean clearPhoto = false;
}
