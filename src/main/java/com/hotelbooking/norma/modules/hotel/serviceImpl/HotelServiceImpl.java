package com.hotelbooking.norma.modules.hotel.serviceImpl;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.request.HotelDto;
import com.hotelbooking.norma.dto.request.UpdateHotelRequest;
import com.hotelbooking.norma.dto.response.HotelResponse;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.cloudinary.service.CloudinaryService;
import com.hotelbooking.norma.modules.hotel.repository.HotelRepository;
import com.hotelbooking.norma.modules.hotel.service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;
        
    @Override
    public ResponseModel addHotel(HotelDto dto) throws IOException, SQLException{
        boolean hotelExists = hotelRepository.existsByNameIgnoreCase(dto.getName());
        if (hotelExists) {
            throw new GlobalRequestException(
                String.format(Message.ALREADY_EXISTS, "Hotel"),
                HttpStatus.CONFLICT
            );
        }
        MultipartFile photoFile = dto.getPhoto();

        Hotel newHotel = modelMapper.map(dto, Hotel.class);
        newHotel.setHotelCode("HOTEL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        
        try {
            String photoUrl = cloudinaryService.uploadImage(photoFile);
            newHotel.setPhotoUrl(photoUrl);
        } catch (IOException e) {
            // Log the error for debugging purposes
            System.err.println("Cloudinary upload failed: " + e.getMessage());
            throw new GlobalRequestException(
                "Failed to upload photo to Cloudinary: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        Hotel savedHotel = hotelRepository.save(newHotel);
        HotelResponse response = modelMapper.map(savedHotel, HotelResponse.class);
        return new ResponseModel(HttpStatus.CREATED.value(), String.format(Message.SUCCESS_CREATE, "Hotel"), response);
    }

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
    public ResponseModel deleteHotelByHotelCode(String hotelCode) {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode).orElseThrow( 
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        hotelRepository.delete(hotel);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_DELETE, "Hotel"), null);
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

    @Override
    public ResponseModel updateHotelByHotelCode(String hotelCode, UpdateHotelRequest dto) {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode)
            .orElseThrow(() -> new GlobalRequestException(
                String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        modelMapper.map(dto, hotel);
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        // 3. Handle photo update logic
        MultipartFile photo = dto.getPhoto();
        String oldPhotoUrl = hotel.getPhotoUrl();

        // Check if a new photo is provided
        if (photo != null && !photo.isEmpty()) {
            try {
                // Delete old photo from Cloudinary if it exists
                if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                    cloudinaryService.deletePhotoByUrl(oldPhotoUrl);
                }

                // Upload the new photo and update the hotel entity
                String newPhotoUrl = cloudinaryService.uploadImage(photo);
                hotel.setPhotoUrl(newPhotoUrl);
                
            } catch (IOException e) {
                log.error("Failed to upload new hotel photo to Cloudinary: " + e.getMessage());
                throw new GlobalRequestException(
                    "Failed to upload new photo: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } 
        // Handle explicit photo clearing
        else if (Boolean.TRUE.equals(dto.getClearPhoto())) {
            if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                try {
                    // Delete the photo from Cloudinary and clear the URL in the database
                    cloudinaryService.deletePhotoByUrl(oldPhotoUrl);
                    hotel.setPhotoUrl(null);
                } catch (IOException e) {
                    log.error("Failed to delete old photo when 'clearPhoto' was true: " + e.getMessage());
                    // Don't throw an error here, just log it and proceed with clearing the database entry
                    hotel.setPhotoUrl(null);
                }
            }
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        HotelResponse hotelResponse = modelMapper.map(savedHotel, HotelResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "Hotel"),
            hotelResponse);
    }
}










