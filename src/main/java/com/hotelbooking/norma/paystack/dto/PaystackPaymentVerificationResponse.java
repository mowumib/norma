package com.hotelbooking.norma.paystack.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaystackPaymentVerificationResponse {
    private boolean status; // Status of the verification (true or false)
    private String message; // Message from Paystack (e.g., "Verification successful")
    private PaymentVerificationResponse data;      // Data object containing detailed transaction info

    @Data
    public static class PaymentVerificationResponse {
        private long id;                     
        private String domain;               
        private String status;               
        private String reference;            
        private String receipt_number;       
        private int amount;                 
        private String gateway_response;     
        private String paid_at;              
        private String created_at;           
        private String channel;              
        private String currency;             
        private String ip_address;           
        private Log log;                     
        private int fees;                    
        private Authorization authorization; 
        private Customer customer;          

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
            private String customer_code;
            private String phone;
            private String metadata;
            private String risk_action;
        }
    }
}
