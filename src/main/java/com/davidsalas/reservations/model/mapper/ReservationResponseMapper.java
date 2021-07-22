package com.davidsalas.reservations.model.mapper;

import com.davidsalas.reservations.model.response.ReservationResponse;
import com.davidsalas.reservations.persistence.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationResponseMapper {

    public ReservationResponse map(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .email(reservation.getEmail())
                .fullName(reservation.getFullName())
                .status(reservation.getStatus())
                .arrivalDate(reservation.getArrivalDate())
                .departureDate(reservation.getDepartureDate())
                .build();
    }
}
