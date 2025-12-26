package com.creditwise.dto;

import lombok.Data;

@Data
public class ClientProfileDto {
    private String id;
    private String userId;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipCode;
}