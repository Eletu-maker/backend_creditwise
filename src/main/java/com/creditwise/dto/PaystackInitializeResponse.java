package com.creditwise.dto;
import lombok.Data;

@Data
public class PaystackInitializeResponse {

    private boolean status;
    private String message;
    private DataResponse data;

    @Data
    public static class DataResponse {

        private String authorization_url;
        private String access_code;
        private String reference;
    }
}