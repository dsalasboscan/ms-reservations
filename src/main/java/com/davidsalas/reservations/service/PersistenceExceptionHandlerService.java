package com.davidsalas.reservations.service;

import com.davidsalas.reservations.exception.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.DATE_ALREADY_RESERVED;
import static com.davidsalas.reservations.util.ErrorMessageConstants.SOME_DATE_ALREADY_RESERVED_ERROR_MSG;

@Component
public class PersistenceExceptionHandlerService {

    public <T> void handle(Supplier<T> process) {
        try {
            process.get();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(DATE_ALREADY_RESERVED.name(), SOME_DATE_ALREADY_RESERVED_ERROR_MSG);
        }
    }
}
