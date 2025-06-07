package com.hotelbooking.norma.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotelbooking.norma.enums.BookingStatus;
import com.hotelbooking.norma.enums.PaymentStatus;
import com.hotelbooking.norma.enums.RoomType;
import com.hotelbooking.norma.utils.BaseEntityAudit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking extends BaseEntityAudit {

    @Column(name = "booking_code", unique = true, nullable = false)
    private String bookingCode;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "room_code", referencedColumnName = "room_code")
    private Room room;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "amount")
    private int amount;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "booking_status")
    private BookingStatus bookingStatus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "hotel_code", referencedColumnName = "hotel_code")
    private Hotel hotel;

    @Column(name = "room_type", nullable = false)   
    private RoomType roomType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_code", referencedColumnName = "user_code")
    private User user;


}
