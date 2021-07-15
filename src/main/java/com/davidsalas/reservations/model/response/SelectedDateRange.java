package com.davidsalas.reservations.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SelectedDateRange {
    private LocalDate from;
    private LocalDate to;
}
