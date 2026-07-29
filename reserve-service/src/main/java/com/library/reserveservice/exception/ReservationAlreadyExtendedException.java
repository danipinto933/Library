package com.library.reserveservice.exception;

public class ReservationAlreadyExtendedException extends RuntimeException {
    public ReservationAlreadyExtendedException(String message) {
        super(message);
    }
}
