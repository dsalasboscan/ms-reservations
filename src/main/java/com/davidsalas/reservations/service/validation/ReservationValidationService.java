package com.davidsalas.reservations.service.validation;

import com.davidsalas.reservations.exception.BadRequestException;
import com.davidsalas.reservations.model.enums.ReservationStatusEnum;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.*;
import static com.davidsalas.reservations.util.ErrorMessageConstants.EXCEEDED_MAXIMUM_STAY_ERROR_MSG;
import static com.davidsalas.reservations.util.ErrorMessageConstants.INVALID_MAX_ANTICIPATION_ERROR_MSG;
import static com.davidsalas.reservations.util.ErrorMessageConstants.INVALID_MIN_ANTICIPATION_ERROR_MSG;
import static com.davidsalas.reservations.util.ErrorMessageConstants.UPDATE_CANCELLED_RESERVATION_ERROR_MSG;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class ReservationValidationService {

    private static final int MAX_STAY_DAYS = 3;

    private static final int MIN_ANTICIPATION_DAYS = 1;
    private static final int MAX_ANTICIPATION_MONTHS = 1;

    private final DateRangeValidationService dateRangeValidationService;

    public ReservationValidationService(DateRangeValidationService dateRangeValidationService) {
        this.dateRangeValidationService = dateRangeValidationService;
    }

    public void validateSelectedDatesToReserve(LocalDate arrivalDate, LocalDate departureDate) {
        dateRangeValidationService.validateDateRange(arrivalDate, departureDate);
        validateAnticipation(arrivalDate);
        validateStayLimitsOnCampsite(arrivalDate, departureDate);
    }

    public void validateUpdateCancelledReservation(ReservationStatusEnum reservationStatus) {
        if (ReservationStatusEnum.CANCELLED.equals(reservationStatus)) {
            throw new BadRequestException(RESERVATION_ALREADY_CANCELLED.name(), UPDATE_CANCELLED_RESERVATION_ERROR_MSG);
        }
    }

    private void validateAnticipation(LocalDate arrivalDate) {
        long anticipationToArriveCampsite = DAYS.between(LocalDate.now(), arrivalDate);
        long maxAnticipationDays = DAYS.between(LocalDate.now(), LocalDate.now().plusMonths(MAX_ANTICIPATION_MONTHS));

        if (anticipationToArriveCampsite < MIN_ANTICIPATION_DAYS) {
            throw new BadRequestException(INVALID_RESERVATION_ANTICIPATION.name(), INVALID_MIN_ANTICIPATION_ERROR_MSG);
        }

        if (anticipationToArriveCampsite > maxAnticipationDays) {
            throw new BadRequestException(INVALID_RESERVATION_ANTICIPATION.name(), INVALID_MAX_ANTICIPATION_ERROR_MSG);
        }
    }

    private void validateStayLimitsOnCampsite(LocalDate arrivalDate, LocalDate departureDate) {
        long daysToStay = DAYS.between(arrivalDate, departureDate) + 1;

        if (daysToStay > MAX_STAY_DAYS) {
            throw new BadRequestException(INVALID_RESERVATION_STAY_TIME.name(), EXCEEDED_MAXIMUM_STAY_ERROR_MSG);
        }
    }
}
