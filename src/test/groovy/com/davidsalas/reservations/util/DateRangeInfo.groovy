package com.davidsalas.reservations.util

import com.davidsalas.reservations.persistence.entity.ReservedDay

import java.time.LocalDate

class DateRangeInfo {
    LocalDate arrivalDate
    LocalDate departureDate
    List<ReservedDay> reservedDays

    DateRangeInfo(LocalDate arrivalDate, LocalDate departureDate, List<ReservedDay> reservedDays) {
        this.arrivalDate = arrivalDate
        this.departureDate = departureDate
        this.reservedDays = reservedDays
    }

    LocalDate getArrivalDate() {
        return arrivalDate
    }

    LocalDate getDepartureDate() {
        return departureDate
    }

    List<ReservedDay> getReservedDays() {
        return reservedDays
    }
}