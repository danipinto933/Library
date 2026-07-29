package com.library.bookservice.exception;

public class GenreAlreadyExistsException extends RuntimeException {
    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
