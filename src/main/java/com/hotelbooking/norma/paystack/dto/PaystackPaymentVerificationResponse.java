package com.hotelbooking.norma.paystack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaystackPaymentVerificationResponse {
    private boolean status; // Status of the verification (true or false)
    private String message; // Message from Paystack (e.g., "Verification successful")
    private PaymentVerificationResponse data;      // Data object containing detailed transaction info

    @Data
    public static class PaymentVerificationResponse {
        private long id;                     // Transaction ID
        private String domain;               // Domain of the transaction (live or test)
        private String status;               // Status of the payment (e.g., success)
        private String reference;            // Transaction reference
        private String receipt_number;       // Receipt number (if any)
        private int amount;                  // Amount paid (in kobo)
        private String gateway_response;     // Gateway response (e.g., "Successful")
        private String paid_at;              // Date/time the payment was made
        private String created_at;           // Date/time the transaction was created
        private String channel;              // Payment channel (e.g., card)
        private String currency;             // Currency (e.g., NGN)
        private String ip_address;           // IP address of the customer
        private String metadata;             // Metadata (if any)
        private Log log;                     // Log of the transaction process
        private int fees;                    // Transaction fees
        private Authorization authorization; // Authorization details for the transaction
        private Customer customer;           // Customer details

        @Data
        public static class Log {
            private long start_time;
            private int time_spent;
            private int attempts;
            private int errors;
            private boolean success;
            private boolean mobile;
            private List<History> history;

            @Data
            public static class History {
                private String type;
                private String message;
                private int time;
            }
        }

        @Data
        public static class Authorization {
            private String authorization_code;
            private String bin;
            private String last4;
            private String exp_month;
            private String exp_year;
            private String channel;
            private String card_type;
            private String bank;
            private String country_code;
            private String brand;
            private boolean reusable;
            private String signature;
            private String account_name;
        }

        @Data
        public static class Customer {
            private long id;
            private String first_name;
            private String last_name;
            private String email;
            private String user_code;
            private String phone;
        }
    }
}
