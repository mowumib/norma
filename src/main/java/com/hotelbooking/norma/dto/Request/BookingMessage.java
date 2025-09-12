package com.hotelbooking.norma.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingMessage implements Serializable {
    private String hotelCode;
    private String userCode;
    private BookingDto dto;    
}