package com.hotelbooking.norma.service;

import java.io.IOException;
import java.sql.SQLException;

import com.hotelbooking.norma.dto.HotelDto;
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.UpdateHotelRequest;

public interface HotelService {
    public ResponseModel addHotel(HotelDto dto) throws IOException, SQLException;

    public ResponseModel getHotelByHotelCode(String hotelCode);

    public ResponseModel getAllHotels();

    public ResponseModel deleteHotelByHotelCode(String hotelCode);

    public ResponseModel getHotelByLocation(String location);
    
    public ResponseModel getHotelByName(String name);

    public ResponseModel updateHotelByHotelCode(String hotelCode, UpdateHotelRequest dto);
}
