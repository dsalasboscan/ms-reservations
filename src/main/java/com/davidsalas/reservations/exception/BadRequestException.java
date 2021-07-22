package com.davidsalas.reservations.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class BadRequestException extends HttpException {

    public BadRequestException(String errorCode, String errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorCode, errorMessage);
    }
}
