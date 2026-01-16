package com.hotelbooking.norma.modules.admin.serviceImpl;

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
import com.hotelbooking.norma.dto.request.RoomDto;
import com.hotelbooking.norma.dto.request.UpdateRoomRequest;
import com.hotelbooking.norma.dto.response.RoomResponse;
import com.hotelbooking.norma.entity.Hotel;
import com.hotelbooking.norma.entity.Room;
import com.hotelbooking.norma.enums.RoomStatus;
import com.hotelbooking.norma.exception.GlobalRequestException;
import com.hotelbooking.norma.exception.Message;
import com.hotelbooking.norma.modules.admin.repository.AdminHotelRepository;
import com.hotelbooking.norma.modules.admin.repository.AdminRoomRepository;
import com.hotelbooking.norma.modules.admin.service.AdminRoomService;
import com.hotelbooking.norma.modules.cloudinary.service.CloudinaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRoomServiceImpl implements AdminRoomService {

    private final CloudinaryService cloudinaryService;

    private final ModelMapper modelMapper;

    private final AdminRoomRepository adminRoomRepository;

    private final AdminHotelRepository adminHotelRepository;
    
    @Override
    public ResponseModel addRoom(String hotelCode, RoomDto dto) throws IOException, SQLException {
        Hotel hotel = adminHotelRepository.findByHotelCode(hotelCode).orElseThrow( () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        boolean roomExists = hotel.getRooms().stream()
            .anyMatch(room -> room.getRoomNumber().equalsIgnoreCase(dto.getRoomNumber()));

        if (roomExists) {
            throw new GlobalRequestException(String.format(Message.ALREADY_EXISTS, "Room"), HttpStatus.CONFLICT);
        }

        MultipartFile photoFile = dto.getPhoto();

        Room room = modelMapper.map(dto, Room.class);
        room.setRoomCode("ROOM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        room.setHotelCode(hotel.getHotelCode());
        room.setRoomStatus(RoomStatus.AVAILABLE);

        try {
            String photoUrl = cloudinaryService.uploadImage(photoFile);
            room.setPhotoUrl(photoUrl);
        } catch (IOException e) {
            // Log the error for debugging purposes
            System.err.println("Cloudinary upload failed: " + e.getMessage());
            throw new GlobalRequestException(
                "Failed to upload photo to Cloudinary: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        Room savedRoom = adminRoomRepository.save(room);
        RoomResponse roomResponse = modelMapper.map(savedRoom, RoomResponse.class);
        return new ResponseModel(HttpStatus.CREATED.value(), String.format(Message.SUCCESS_CREATE, "Room"), roomResponse);
    }

    
    @Override
    public ResponseModel getRoomByRoomCode(String roomCode) {
        Room room = adminRoomRepository.findByRoomCode(roomCode).orElseThrow(
        () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        RoomResponse roomResponse = modelMapper.map(room, RoomResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_GET, "Room"), roomResponse);
    }

    @Override
    public ResponseModel getAllAvailableRoom(String hotelCode) {
        Hotel hotel = adminHotelRepository.findByHotelCode(hotelCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));
        
        List<Room> rooms = hotel.getRooms();

        List<RoomResponse> availableRooms = rooms.stream()
            .filter(room -> room != null && room.getRoomStatus() != null && room.getRoomStatus().equals(RoomStatus.AVAILABLE))
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
        Hotel hotel = adminHotelRepository.findByHotelCode(hotelCode)
        .orElseThrow(() -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Hotel"), HttpStatus.NOT_FOUND));

        List<Room> rooms = hotel.getRooms();

        List<RoomResponse> bookedRooms = rooms.stream()
            .filter(room -> room != null && room.getRoomStatus() != null && room.getRoomStatus().equals(RoomStatus.BOOKED))
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
        Hotel hotel = adminHotelRepository.findByHotelCode(hotelCode)
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
        Room room = adminRoomRepository.findByRoomCode(roomCode).orElseThrow(
            () -> new GlobalRequestException(String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));
        adminRoomRepository.delete(room);
        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_DELETE, "Room"), null);
    }


    @Override
    public ResponseModel updateRoomByRoomCode(String roomCode, UpdateRoomRequest dto) {
        Room room = adminRoomRepository.findByRoomCode(roomCode)
        .orElseThrow(() -> new GlobalRequestException(
            String.format(Message.NOT_FOUND, "Room"), HttpStatus.NOT_FOUND));

        modelMapper.map(dto, room);
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        MultipartFile photo = dto.getPhoto();
        String oldPhotoUrl = room.getPhotoUrl();

        // Check if a new photo is provided
        if (photo != null && !photo.isEmpty()) {
            try {
                // Delete old photo from Cloudinary if it exists
                if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()) {
                    cloudinaryService.deletePhotoByUrl(oldPhotoUrl);
                }

                // Upload the new photo and update the hotel entity
                String newPhotoUrl = cloudinaryService.uploadImage(photo);
                room.setPhotoUrl(newPhotoUrl);
                
            } catch (IOException e) {
                log.error("Failed to upload new room photo to Cloudinary: " + e.getMessage());
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
                    room.setPhotoUrl(null);
                } catch (IOException e) {
                    log.error("Failed to delete old photo when 'clearPhoto' was true: " + e.getMessage());
                    // Don't throw an error here, just log it and proceed with clearing the database entry
                    room.setPhotoUrl(null);
                }
            }
        }
        Room savedRoom = adminRoomRepository.save(room);
        RoomResponse roomResponse = modelMapper.map(savedRoom, RoomResponse.class);

        return new ResponseModel(HttpStatus.OK.value(), String.format(Message.SUCCESS_UPDATE, "Room"), roomResponse);
    }
}
