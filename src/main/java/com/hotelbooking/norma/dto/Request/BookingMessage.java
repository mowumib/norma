package com.hotelbooking.norma.dto.Request;

import java.io.Serializable;

import com.hotelbooking.norma.dto.BookingDto;

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