package com.davidsalas.reservations.service;

import com.davidsalas.reservations.exception.BadRequestException;
import com.davidsalas.reservations.exception.NotFountException;
import com.davidsalas.reservations.model.enums.ReservationStatusEnum;
import com.davidsalas.reservations.model.mapper.ReservationResponseMapper;
import com.davidsalas.reservations.model.mapper.UpdateReservationMapper;
import com.davidsalas.reservations.model.request.CreateReservationRequest;
import com.davidsalas.reservations.model.request.UpdateReservationRequest;
import com.davidsalas.reservations.model.response.ReservationResponse;
import com.davidsalas.reservations.persistence.entity.Reservation;
import com.davidsalas.reservations.persistence.entity.ReservedDay;
import com.davidsalas.reservations.persistence.repository.ReservationRepository;
import com.davidsalas.reservations.service.validation.ReservationValidationService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.DATE_ALREADY_RESERVED;
import static com.davidsalas.reservations.model.enums.ErrorCodeEnum.RESERVATION_NOT_FOUND;
import static com.davidsalas.reservations.util.DatabaseConstants.RESERVED_DAY_UNIQUE_CONSTRAINT_NAME;
import static com.davidsalas.reservations.util.ErrorMessageConstants.RESERVATION_NOT_FOUND_ERROR_MSG;
import static com.davidsalas.reservations.util.ErrorMessageConstants.SOME_DATE_ALREADY_RESERVED_ERROR_MSG;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationResponseMapper reservationResponseMapper;

    private final UpdateReservationMapper updateReservationMapper;

    private final ReservationValidationService reservationValidationService;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationResponseMapper reservationResponseMapper,
                              UpdateReservationMapper updateReservationMapper,
                              ReservationValidationService reservationValidationService) {
        this.reservationRepository = reservationRepository;
        this.reservationResponseMapper = reservationResponseMapper;
        this.updateReservationMapper = updateReservationMapper;
        this.reservationValidationService = reservationValidationService;
    }

    public ReservationResponse searchReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFountException(RESERVATION_NOT_FOUND.name(), String.format(RESERVATION_NOT_FOUND_ERROR_MSG, id)));
        return reservationResponseMapper.map(reservation);
    }

    @Transactional
    public ReservationResponse reserveCampsite(CreateReservationRequest request) {
        LocalDate arrivalDate = request.getArrivalDate();
        LocalDate departureDate = request.getDepartureDate();

        reservationValidationService.validateSelectedDatesToReserve(arrivalDate, departureDate);

        List<ReservedDay> daysToReserve = arrivalDate.datesUntil(departureDate.plusDays(1)).map(ReservedDay::new).collect(Collectors.toList());

        Reservation reservation = new Reservation(request.getFullName(), request.getEmail(), arrivalDate, departureDate, daysToReserve);

        handleReservationDbOperation(() -> reservationRepository.save(reservation));

        return reservationResponseMapper.map(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long id, UpdateReservationRequest request) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFountException(RESERVATION_NOT_FOUND.name(), String.format(RESERVATION_NOT_FOUND_ERROR_MSG, id)));

        reservationValidationService.validateSelectedDatesToReserve(request.getArrivalDate(), request.getDepartureDate());

        reservationValidationService.validateUpdateCancelledReservation(reservation.getStatus());

        Reservation updatedReservation = updateReservationMapper.map(reservation, request);

        handleReservationDbOperation(() -> reservationRepository.save(updatedReservation));

        return reservationResponseMapper.map(updatedReservation);
    }

    @Transactional
    public ReservationResponse deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new NotFountException(RESERVATION_NOT_FOUND.name(), String.format(RESERVATION_NOT_FOUND_ERROR_MSG, id)));

        reservation.setStatus(ReservationStatusEnum.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.getReservedDays().clear();

        handleReservationDbOperation(() -> reservationRepository.save(reservation));

        return reservationResponseMapper.map(reservation);
    }

    private <T> void handleReservationDbOperation(Supplier<T> process) {
        try {
            process.get();
        } catch (DataIntegrityViolationException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                throw e;
            }

            ConstraintViolationException constraintEx = (ConstraintViolationException) e.getCause();

            Throwable sqlServerException = constraintEx.getCause();

            if (sqlServerException.getMessage().contains(RESERVED_DAY_UNIQUE_CONSTRAINT_NAME)) {
                throw new BadRequestException(DATE_ALREADY_RESERVED.name(), SOME_DATE_ALREADY_RESERVED_ERROR_MSG);
            }

            throw e;
        }
    }
}
