package com.hotelbooking.norma.modules.hotel.serviceImpl;


import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.response.HotelResponse;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.hotel.repository.HotelRepository;
import com.hotelbooking.norma.modules.hotel.service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
        
    @Override
    public ResponseModel getHotelByHotelCode(String hotelCode) {
        Hotel hotel =hotelRepository.findByHotelCode(hotelCode).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        HotelResponse hotelResponse = modelMapper.map(hotel, HotelResponse.class);


        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Hotel"), hotelResponse);
    }

    @Override
    public ResponseModel getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelResponse> hotelResponses = hotels.stream()
            .map(hotel -> modelMapper.map(hotel, HotelResponse.class))
            .collect(Collectors.toList());

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Hotels"), hotelResponses);  
    }
    
    @Override
    public ResponseModel getHotelByLocation(String location) {
        List<Hotel> hotels = hotelRepository.findByLocation(location);
        if (hotels.isEmpty()) {
            throw new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotels"), HttpStatus.NOT_FOUND);
        }

        List<HotelResponse> hotelResponses = hotels.stream()
            .map(hotel -> modelMapper.map(hotel, HotelResponse.class))
            .collect(Collectors.toList());
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Hotels"), hotelResponses);
    }

    @Override
    public ResponseModel getHotelByName(String name) {
        Hotel hotel = hotelRepository.findByName(name).orElseThrow(() ->
            new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));
        
        HotelResponse response = modelMapper.map(hotel, HotelResponse.class);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Hotel"), response);
    }

}










