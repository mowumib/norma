package com.hotelbooking.norma.paystack.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaystackTransactionFetchResponse {
    private boolean status;
    private String message;
    private TransactionData data;

    @Data
    public static class TransactionData {

        private long id;
        private String domain;
        private String status;
        private String reference;
        private String receipt_number;
        private int amount;
        private String message;
        private String gateway_response;
        private String helpdesk_link;
        private String paidAt;
        private String createdAt;
        private String channel;
        private String currency;
        private String ip_address;
        private String metadata;
        private Log log;
        private int fees;
        private Object fees_split;
        private Authorization authorization;
        private Customer customer;
        private Object plan;
        private Object subaccount;
        private Object split;
        private String order_id;
        private int requested_amount;
        private Object pos_transaction_data;
        private Source source;
        private Object fees_breakdown;
        private Object connect;

        @Data
        public static class Log {
            private int start_time;
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
            private String accountName;
        }

        @Data
        public static class Customer {
            private long id;
            private String first_name;
            private String last_name;
            private String email;
            private String customer_code;
            private String phone;
            private CustomerMetadata metadata;
            private String risk_action;

            @Data
            public static class CustomerMetadata {
                @JsonProperty("custom_fields")
                private List<CustomField> customFields;

                @Data
                public static class CustomField {
                    private String display_name;
                    private String variable_name;
                    private String value;
                }
            }
        }

        @Data
        public static class Source {
            private String type;
            private String source;
            private String identifier;
            private String entry_point;
        }
    }
}