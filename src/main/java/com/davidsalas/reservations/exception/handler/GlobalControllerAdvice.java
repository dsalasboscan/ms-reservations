package com.davidsalas.reservations.exception.handler;

import com.davidsalas.reservations.exception.HttpException;
import com.davidsalas.reservations.model.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Date;

import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.INVALID_REQUEST_PARAMETER;
import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.UNEXPECTED_EXCEPTION;

@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpException ex) {
        LOGGER.error("Exception: {} is thrown, the reservation process could not be completed", ex.getClass().getCanonicalName());
        return new ResponseEntity<>(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage()), ex.getHttpStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_REQUEST_PARAMETER.name(), e.getMessage()));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_REQUEST_PARAMETER.name(), e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(INVALID_REQUEST_PARAMETER.name(), e.getMessage()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        LOGGER.warn("handling validation exception: {}", e.getLocalizedMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(UNEXPECTED_EXCEPTION.name(), e.getMessage()));
    }
}
