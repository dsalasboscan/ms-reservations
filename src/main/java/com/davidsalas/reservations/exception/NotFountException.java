package com.davidsalas.reservations.exception;

import com.davidsalas.reservations.exception.HttpException;
import org.springframework.http.HttpStatus;

public class NotFountException extends HttpException {
    public NotFountException(String errorCode, String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorCode, errorMessage);
    }
}
