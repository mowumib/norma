package com.hotelbooking.norma.paystack.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaystackTransactionListResponse {

    private boolean status;
    private String message;
    private List<TransactionData> data;
    private Meta meta; // Metadata for pagination

    @Data
    public static class TransactionData {
        private long id;
        private String domain;
        private String status;
        private String reference;
        private int amount;
        private String message;
        private String gateway_response;
        private String paid_At;
        private String created_At;
        private String channel;
        private String currency;
        private String ip_address;
        private Object metadata;
        private Log log;
        private int fees;
        private Customer customer;
        private Authorization authorization;
        private Plan plan;
        private Split split;
        private Subaccount subaccount;
        private String order_id;
        private String paidAt;
        private String createdAt;
        private int requested_amount;
        private Source source;
        private Object connect;
        private Object posTransactionData;

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
        public static class Customer {
            private long id;
            private String first_name;
            private String last_name;
            private String email;
            private String phone;
            private CustomerMetadata metadata;
            private String customer_code;
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
        public static class Plan {
            private Object plan;
        }

        @Data
        public static class Split {
            private Object split;
        }

        @Data
        public static class Subaccount {
            private Object subaccount;
        }

        @Data
        public static class Source {
            private String source;
            private String type;
            private String identifier;
            private String entry_point;
        }
    }

    @Data
    public static class Meta {
        private String next;
        private String previous;
        private int perPage;
    }
}
