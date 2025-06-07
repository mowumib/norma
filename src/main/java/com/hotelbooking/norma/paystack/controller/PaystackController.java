package com.hotelbooking.norma.paystack.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotelbooking.norma.dto.ResponseModel;
import com.hotelbooking.norma.paystack.dto.PaystackPaymentDto;
import com.hotelbooking.norma.paystack.service.PaystackService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/paystack/transaction")
@RequiredArgsConstructor
public class PaystackController {

    private final PaystackService paystackService;

    @PostMapping("/initialize")
    public ResponseEntity<ResponseModel> initializePayment(@RequestBody PaystackPaymentDto dto) {
        ResponseModel responseModel = paystackService.initializeTransaction(dto);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<ResponseModel> verifyPayment(@PathVariable String reference) {
        ResponseModel responseModel = paystackService.verifyTransaction(reference);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel> listTransactions(@RequestParam(defaultValue = "50") int perPage,
                                                          @RequestParam(required = false) String nextPage) {
        ResponseModel responseModel = paystackService.listTransactions(perPage, nextPage);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<ResponseModel> fetchTransactionByReference(
            @PathVariable("reference") String reference) {
        ResponseModel responseModel = paystackService.fetchTransaction(reference);
        return ResponseEntity.status(responseModel.getStatusCode()).body(responseModel);
    }

}
