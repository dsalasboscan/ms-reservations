package com.davidsalas.reservations.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public abstract class HttpException extends RuntimeException {
    private HttpStatus httpStatus;
    private String errorCode;
    private String errorMessage;
}
