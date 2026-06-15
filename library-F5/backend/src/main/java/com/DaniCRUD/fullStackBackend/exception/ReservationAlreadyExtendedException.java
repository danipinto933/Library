package com.DaniCRUD.fullStackBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ReservationAlreadyExtendedException extends RuntimeException {
    public ReservationAlreadyExtendedException(String message) {
        super(message);
    }
}
