package com.creditwise.exception;

public class OfficerCapacityExceededException extends RuntimeException {
    public OfficerCapacityExceededException(String message) {
        super(message);
    }
}