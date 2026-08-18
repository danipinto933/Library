package com.library.reserveservice.exception;

public class MaxReservationLimitExceededException extends RuntimeException {
    public MaxReservationLimitExceededException(String message) {
        super(message);
    }
}
