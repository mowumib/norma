package com.hotelbooking.norma.paystack.service;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentDto;

public interface PaystackService {
    ResponseModel initializeTransaction(PaystackPaymentDto dto);
    
    ResponseModel verifyTransaction(String reference);

    ResponseModel listTransactions(int perPage, String nextPage);

    ResponseModel fetchTransaction(String reference);

    // ResponseModel fetchTransactions(String loanCaseCode);

}

