package com.hotelbooking.norma.modules.hotel.service;

import com.hotelbooking.norma.dto.ResponseModel;

public interface HotelService {

    public ResponseModel getHotelByHotelCode(String hotelCode);

    public ResponseModel getAllHotels(); //ADD FILTERS AND PAGINATION, LOCATIONS

    public ResponseModel getHotelByLocation(String location);
    
    public ResponseModel getHotelByName(String name);
}
