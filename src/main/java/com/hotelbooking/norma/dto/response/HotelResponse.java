package com.hotelbooking.norma.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelResponse {
    private Long id;
    private String hotelCode;
    private String name;
    private String location;
    private String photoUrl;
}