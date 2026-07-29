package com.library.reserveservice.exception;

public class InvalidReservationDatesException extends RuntimeException {
    public InvalidReservationDatesException(String message) {
        super(message);
    }
}
