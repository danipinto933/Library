package com.DaniCRUD.fullStackBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationErrorsException extends RuntimeException
{
    public ValidationErrorsException (String message)
    {
        super(message);
    }
}
