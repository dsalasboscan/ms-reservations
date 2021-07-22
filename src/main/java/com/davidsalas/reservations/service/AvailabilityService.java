package com.davidsalas.reservations.service;

import com.davidsalas.reservations.model.response.SelectedDateRange;
import com.davidsalas.reservations.model.response.AvailabilityResponse;
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository;
import com.davidsalas.reservations.service.validation.DateRangeValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityService.class);

    private final AvailabilityRepository availabilityRepository;
    private final DateRangeValidationService dateRangeValidationService;

    public AvailabilityService(AvailabilityRepository availabilityRepository, DateRangeValidationService dateRangeValidationService) {
        this.availabilityRepository = availabilityRepository;
        this.dateRangeValidationService = dateRangeValidationService;
    }

    public AvailabilityResponse checkAvailability(LocalDate from, LocalDate to) {
        SelectedDateRange selectedDateRange = createDateRange(from, to);

        LOGGER.info("Init check of availability of campsite between date range from {} to {}",
                selectedDateRange.getFrom(), selectedDateRange.getTo());

        dateRangeValidationService.validateDateRange(selectedDateRange.getFrom(), selectedDateRange.getTo());

        List<LocalDate> selectedDates = selectedDateRange.getFrom()
                .datesUntil(selectedDateRange.getTo().plusDays(1)).collect(Collectors.toList());

        List<LocalDate> unavailableDates = availabilityRepository.getUnavailableDates(selectedDateRange.getFrom(),
                selectedDateRange.getTo());

        List<LocalDate> availableDates = selectedDates.stream()
                .filter(it -> !unavailableDates.contains(it)).collect(Collectors.toList());

        return new AvailabilityResponse(selectedDateRange, availableDates);
    }

    public SelectedDateRange createDateRange(LocalDate from, LocalDate to) {
        from = Optional.ofNullable(from).orElse(LocalDate.now().plusDays(1));
        to = Optional.ofNullable(to).orElse(from.plusMonths(1));
        return new SelectedDateRange(from, to);
    }
}
