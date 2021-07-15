package com.davidsalas.reservations.model.mapper;

import com.davidsalas.reservations.model.request.UpdateReservationRequest;
import com.davidsalas.reservations.persistence.entity.Reservation;
import com.davidsalas.reservations.persistence.entity.ReservedDay;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UpdateReservationMapper {

    public Reservation map(Reservation reservation, UpdateReservationRequest updateReservationRequest) {
        return updateReservation(reservation, updateReservationRequest);
    }

    private Reservation updateReservation(Reservation reservation, UpdateReservationRequest request) {
        Reservation updatedReservation = new Reservation(reservation.getFullName(), reservation.getEmail(),
                reservation.getArrivalDate(), reservation.getDepartureDate(), reservation.getReservedDays());

        updatedReservation.setId(reservation.getId());
        updatedReservation.setCreatedAt(reservation.getCreatedAt());
        updatedReservation.setUpdatedAt(reservation.getUpdatedAt());

        String newEmail = request.getEmail();
        if (!Objects.isNull(newEmail)) {
            updatedReservation.setEmail(newEmail);
        }

        String newFullName = request.getFullName();
        if (!Objects.isNull(newFullName)) {
            updatedReservation.setFullName(newFullName);
        }

        LocalDate arrivalDate = request.getArrivalDate();
        LocalDate departureDate = request.getDepartureDate();

        if (!Objects.isNull(arrivalDate) && !Objects.isNull(departureDate)) {
            updatedReservation.setArrivalDate(arrivalDate);
            updatedReservation.setDepartureDate(departureDate);
            List<ReservedDay> reservedDays = getReservedDays(arrivalDate, departureDate);

            if (!reservedDays.equals(updatedReservation.getReservedDays())) {
                updatedReservation.getReservedDays().clear();
                updatedReservation.setReservedDays(reservedDays);
            }
        }
        return updatedReservation;
    }

    private List<ReservedDay> getReservedDays(LocalDate arrivalDate, LocalDate departureDate) {
        return arrivalDate.datesUntil(departureDate.plusDays(1)).map(ReservedDay::new).collect(Collectors.toList());
    }
}
