package com.hotelbooking.norma.serviceImpl;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.Value;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.hotelbooking.norma.dto.HotelDto;
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.Request.UpdateHotelRequest;
import com.hotelbooking.norma.dto.response.HotelResponse;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.repository.HotelRepository;
import com.hotelbooking.norma.service.HotelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    @Value("${googleDrive.hotel-images-folder-id}")
    private String imagesFolderId;

    private final HotelRepository hotelRepository;
    private final Drive googleDriveService;
    private final ModelMapper modelMapper;

    private final String HOTEL_IMAGES_FOLDER_ID = imagesFolderId;
        
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
        
        if (photoFile != null && !photoFile.isEmpty()) {
            // 1. Upload photo to Google Drive
            try {
                // Metadata for the file
                File fileMetadata = new File();
                fileMetadata.setName(photoFile.getOriginalFilename()); // Or generate a unique name
                fileMetadata.setParents(Collections.singletonList(HOTEL_IMAGES_FOLDER_ID)); // OPTIONAL: Upload to a specific folder

                InputStreamContent content = new InputStreamContent(photoFile.getContentType(),
                        new ByteArrayInputStream(photoFile.getBytes()));

                File uploadedFile = googleDriveService.files().create(fileMetadata, content)
                        .setFields("id, webViewLink, mimeType") // Request specific fields
                        .execute();

                // Make the file publicly accessible
                com.google.api.services.drive.model.Permission newPermission = new com.google.api.services.drive.model.Permission()
                        .setType("anyone")
                        .setRole("reader");
                googleDriveService.permissions().create(uploadedFile.getId(), newPermission).execute();

                newHotel.setGoogleDriveFileId(uploadedFile.getId());
                newHotel.setPhotoUrl(uploadedFile.getWebViewLink()); // This is the public link
                newHotel.setPhotoContentType(uploadedFile.getMimeType()); // Store content type
            } catch (IOException e) {
                // Handle upload error, e.g., log it and rethrow a custom exception
                throw new GlobalRequestException("Failed to upload photo to Google Drive: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), String.format(Message.EMPTY_PHOTO), null);
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
        // If a new photo file is provided and not empty
        if (photo != null && !photo.isEmpty()) {
            try {
                // Delete old photo from Google Drive (if one exists)
                Optional.ofNullable(hotel.getGoogleDriveFileId()).ifPresent(oldFileId -> {
                    try {
                        googleDriveService.files().delete(oldFileId).execute();
                        System.out.println("Old hotel photo deleted from Google Drive: " + oldFileId);
                    } catch (IOException e) {
                        log.error("Failed to delete old hotel photo from Google Drive: " + oldFileId + ", Error: " + e.getMessage());
                    }
                });

                // Upload new photo to Google Drive
                File fileMetadata = new File();
                fileMetadata.setName(photo.getOriginalFilename()); // Or generate a unique name
                fileMetadata.setParents(Collections.singletonList(HOTEL_IMAGES_FOLDER_ID));

                InputStreamContent content = new InputStreamContent(photo.getContentType(),
                        new ByteArrayInputStream(photo.getBytes()));

                File uploadedFile = googleDriveService.files().create(fileMetadata, content)
                        .setFields("id, webViewLink, mimeType")
                        .execute();

                // Make the new file publicly accessible
                Permission newPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader"); // Use "reader" for public access
                googleDriveService.permissions().create(uploadedFile.getId(), newPermission).execute();

                // Update hotel entity with new Google Drive file details
                hotel.setGoogleDriveFileId(uploadedFile.getId());
                hotel.setPhotoUrl(uploadedFile.getWebViewLink());
                hotel.setPhotoContentType(uploadedFile.getMimeType());

            } catch (IOException e) {
                throw new GlobalRequestException("Failed to upload new hotel photo to Google Drive: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } 
        // Handle explicit photo clearing: If 'clearPhoto' flag is true AND no new photo was provided
        else if (Boolean.TRUE.equals(dto.getClearPhoto())) {
            if(hotel.getGoogleDriveFileId() != null){
                try {
                    googleDriveService.files().delete(hotel.getGoogleDriveFileId()).execute();
                    hotel.setGoogleDriveFileId(null);
                    hotel.setPhotoUrl(null);
                    hotel.setPhotoContentType(null);
                    System.out.println("Hotel photo explicitly removed for hotel: " + hotelCode);
                } catch (IOException e) {
                    log.error("Failed to delete old photo when 'clearPhoto' was true: " + e.getLocalizedMessage());
                }
            }
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        HotelResponse hotelResponse = modelMapper.map(savedHotel, HotelResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "Hotel"),
            hotelResponse);
    }
}










