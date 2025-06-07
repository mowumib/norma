package com.hotelbooking.norma.entity;

import java.util.List;

import com.hotelbooking.norma.enums.RoomType;
import com.hotelbooking.norma.enums.Status;
import com.hotelbooking.norma.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room extends BaseEntity{

    @Column(name = "room_code", unique = true, nullable = false)
    private String roomCode;

    @Column(name = "room_type", nullable = false)   
    private RoomType roomType;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "room_price", nullable = false)
    private Integer roomPrice;
    
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "room_description", nullable = false)
    private String roomDescription;
    
    @Column(name = "hotel_code")
    private String hotelCode;

    @Column(name = "google_drive_file_id")
    private String googleDriveFileId;

    @Column(name = "photo_url")
    private String photoUrl;  

    @Column(name = "photo_content_type")
    private String photoContentType;

    // @Lob
    // private Blob photo;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

}
