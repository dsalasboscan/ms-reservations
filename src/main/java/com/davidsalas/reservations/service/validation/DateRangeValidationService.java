package com.davidsalas.reservations.service.validation;

import com.davidsalas.reservations.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.INVALID_RESERVATION_DATE_RANGE;
import static com.davidsalas.reservations.util.ErrorMessageConstants.*;

@Service
public class DateRangeValidationService {

    public void validateDateRange(LocalDate arrivalDate, LocalDate departureDate) {
        LocalDate currentDate = LocalDate.now();
        if (arrivalDate.isAfter(departureDate)) {
            throw new BadRequestException(INVALID_RESERVATION_DATE_RANGE.name(), String.format(INVALID_DATE_RANGE_ERROR_MSG, arrivalDate, departureDate));
        }

        if (departureDate.equals(arrivalDate)) {
            throw new BadRequestException(INVALID_RESERVATION_DATE_RANGE.name(), INVALID_DATE_RANGE_MUST_NOT_BE_EQUAL);
        }

        if (arrivalDate.isBefore(currentDate) || departureDate.isBefore(currentDate)) {
            throw new BadRequestException(INVALID_RESERVATION_DATE_RANGE.name(), INVALID_DATE_RANGE_SHOULD_BE_IN_FUTURE);
        }
    }
}
