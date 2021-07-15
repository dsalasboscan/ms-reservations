package com.davidsalas.reservations.model.enums;

public enum ReservationStatusEnum {
    ACTIVE(0L),
    CANCELLED(1L);

    Long id;

    ReservationStatusEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
