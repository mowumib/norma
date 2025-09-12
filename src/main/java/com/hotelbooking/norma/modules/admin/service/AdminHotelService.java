package com.hotelbooking.norma.modules.admin.service;

import java.io.IOException;
import java.sql.SQLException;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.request.HotelDto;
import com.hotelbooking.norma.dto.request.UpdateHotelRequest;

public interface AdminHotelService {

    public ResponseModel addHotel(HotelDto dto) throws IOException, SQLException;

    public ResponseModel getHotelByHotelCode(String hotelCode);

    public ResponseModel getAllHotels(); //ADD FILTERS AND PAGINATION, LOCATIONS

    public ResponseModel deleteHotelByHotelCode(String hotelCode);

    public ResponseModel updateHotelByHotelCode(String hotelCode, UpdateHotelRequest dto);
}
