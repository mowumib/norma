package com.hotelbooking.norma.dto.response;

import com.hotelbooking.norma.enums.RoomType;
import com.hotelbooking.norma.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomCode;
    private RoomType roomType;
    private String roomNumber;
    private Integer roomPrice;
    private Status status;
    private String roomDescription;
    private String hotelCode;
    private String photoURL;
}

