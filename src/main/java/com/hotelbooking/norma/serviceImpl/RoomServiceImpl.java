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
import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.dto.RoomDto;
import com.hotelbooking.norma.dto.Request.UpdateRoomRequest;
import com.hotelbooking.norma.dto.response.RoomResponse;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.entity.Room;
import com.hotelbooking.norma.enums.Status;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.repository.HotelRepository;
import com.hotelbooking.norma.repository.RoomRepository;
import com.hotelbooking.norma.service.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Value ("${googleDrive.room-images-folder-id}")
    private String imagesFolderId;

    private final HotelRepository hotelRepository;

    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;

    private final String ROOM_IMAGES_FOLDER_ID = imagesFolderId;
        
    private final Drive googleDriveService;
    @Override
    public ResponseModel addRoom(String hotelCode, RoomDto dto) throws IOException, SQLException {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        boolean roomExists = hotel.getRooms().stream()
            .anyMatch(room -> room.getRoomNumber().equalsIgnoreCase(dto.getRoomNumber()));

        if (roomExists) {
            throw new GlobalRequestException(String.format(Message.ALREADY_EXISTS, "Room"), HttpStatus.CONFLICT);
        }

        MultipartFile photoFile = dto.getPhoto();

        Room room = modelMapper.map(dto, Room.class);
        room.setRoomCode("ROOM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        room.setHotelCode(hotel.getHotelCode());
        room.setStatus(Status.AVAILABLE);
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                // Metadata for the file
                File fileMetadata = new File();
                fileMetadata.setName(photoFile.getOriginalFilename()); // Or generate a unique name
                fileMetadata.setParents(Collections.singletonList(ROOM_IMAGES_FOLDER_ID)); // OPTIONAL: Upload to a specific folder

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

                room.setGoogleDriveFileId(uploadedFile.getId());
                room.setPhotoUrl(uploadedFile.getWebViewLink()); // This is the public link
                room.setPhotoContentType(uploadedFile.getMimeType()); // Store content type
            } catch (IOException e) {
                // Handle upload error, e.g., log it and rethrow a custom exception
                throw new GlobalRequestException("Failed to upload photo to Google Drive: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseModel(HttpStatus.BAD_REQUEST.value(), String.format(Message.EMPTY_PHOTO), null);
        }
        Room savedRoom = roomRepository.save(room);
        RoomResponse roomResponse = modelMapper.map(savedRoom, RoomResponse.class);
        return new ResponseModel(HttpStatus.CREATED.value(), String.format(Message.SUCCESS_CREATE, "Room"), roomResponse);
    }

    
    @Override
    public ResponseModel getRoomByRoomCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow(
        () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Room"), roomResponse);
    }

    @Override
    public ResponseModel getAllAvailableRoom(String hotelCode) {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));
        
        List<Room> rooms = hotel.getRooms();

        List<RoomResponse> availableRooms = rooms.stream()
            .filter(room -> room != null && room.getStatus() != null && room.getStatus().equals(Status.AVAILABLE))
            .map(room -> modelMapper.map(room, RoomResponse.class))
            .collect(Collectors.toList());
        
        if (availableRooms.isEmpty()) {
            return new ResponseModel(
                HttpStatus.OK.value(),
                String.format(Message.NOT_FOUND, "Available Rooms"), availableRooms
            );
        }
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Rooms"), availableRooms);
    }

    @Override
    public ResponseModel getAllBookedRoom(String hotelCode) {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode)
        .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        List<Room> rooms = hotel.getRooms();

        List<RoomResponse> bookedRooms = rooms.stream()
            .filter(room -> room != null && room.getStatus() != null && room.getStatus().equals(Status.BOOKED))
            .map(room -> modelMapper.map(room, RoomResponse.class))
            .collect(Collectors.toList());
        
        if (bookedRooms.isEmpty()) {
            return new ResponseModel(
                HttpStatus.OK.value(),
                String.format(Message.NOT_FOUND, "Booked Rooms"), bookedRooms
            );
        }
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Rooms"),
            bookedRooms);
    }


    @Override
    public ResponseModel getAllRooms(String hotelCode) {
        Hotel hotel = hotelRepository.findByHotelCode(hotelCode)
        .orElseThrow(() -> new GlobalRequestException(
            String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        List<Room> rooms = hotel.getRooms();
        List<RoomResponse> roomData = rooms.stream()
            .filter(room -> room != null)
            .map(room -> modelMapper.map(room, RoomResponse.class))
            .collect(Collectors.toList());

        return new ResponseModel(
            HttpStatus.OK.value(),
            String.format(Message.SUCCESS_GET, "Rooms"), roomData);
    }

    
    @Override
    public ResponseModel deleteRoomByRoomCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));
        roomRepository.delete(room);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_DELETE, "Room"), null);
    }


    @Override
    public ResponseModel updateRoomByRoomCode(String roomCode, UpdateRoomRequest dto) {
        Room room = roomRepository.findByRoomCode(roomCode)
        .orElseThrow(() -> new GlobalRequestException(
            String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        modelMapper.map(dto, room);
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        MultipartFile photo = dto.getPhoto();
        if(photo != null && !photo.isEmpty()) {
            try{
                Optional.ofNullable(room.getGoogleDriveFileId()).ifPresent(oldFileId -> {
                try {
                    googleDriveService.files().delete(oldFileId).execute();
                    System.out.println("Old file deleted from Google Drive: " + oldFileId);
                } catch (IOException e) {
                    // Log the error but don't prevent the new upload
                    log.error("Failed to delete old hotel photo from Google Drive: " + oldFileId + ", Error: " + e.getMessage());
                }
                });

                // Upload the new photo to Google Drive
                File fileMetadata = new File();
                fileMetadata.setName(photo.getOriginalFilename());
                fileMetadata.setParents(Collections.singletonList(ROOM_IMAGES_FOLDER_ID));

                InputStreamContent content = new InputStreamContent(photo.getContentType(),
                    new ByteArrayInputStream(photo.getBytes()));

                File uploadedFile = googleDriveService.files().create(fileMetadata, content)
                    .setFields("id, webViewLink, mimeType")
                    .execute();

                // Make the new file publicly accessible
                Permission newPermission = new Permission()
                    .setType("anyone")
                    .setRole("reader");
                googleDriveService.permissions().create(uploadedFile.getId(), newPermission).execute();

                // Update Room entity with new Google Drive file details
                room.setGoogleDriveFileId(uploadedFile.getId());
                room.setPhotoUrl(uploadedFile.getWebViewLink());
                room.setPhotoContentType(uploadedFile.getMimeType());
            } catch (IOException e) {
                throw new GlobalRequestException("Failed to upload new room photo", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        
        // Handle explicit photo clearing: If 'clearPhoto' flag is true AND no new photo was provided
        else if (Boolean.TRUE.equals(dto.getClearPhoto())) {
            if (room.getGoogleDriveFileId() != null) {
                try {
                    googleDriveService.files().delete(room.getGoogleDriveFileId()).execute();
                    room.setGoogleDriveFileId(null);
                    room.setPhotoUrl(null);
                    room.setPhotoContentType(null);
                    System.out.println("Photo explicitly cleared from Google Drive.");
                } catch (IOException e) {
                    log.error("Failed to delete old photo when 'clearPhoto' was true: " + e.getLocalizedMessage());
                }
            }
        }
        Room savedRoom = roomRepository.save(room);
        RoomResponse roomResponse = modelMapper.map(savedRoom, RoomResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "Room"), roomResponse);
    }

}










