package com.hotelbooking.norma.dto.Request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMessage implements Serializable {
    private String bookingCode;
    private String email;
    private int amount;

}