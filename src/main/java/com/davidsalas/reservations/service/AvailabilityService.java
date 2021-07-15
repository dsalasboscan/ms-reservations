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

        dateRangeValidationService.validateDateRange(selectedDateRange.getFrom(), selectedDateRange.getTo());

        List<LocalDate> selectedDates = selectedDateRange.getFrom()
                .datesUntil(selectedDateRange.getTo().plusDays(1)).collect(Collectors.toList());

        List<LocalDate> unavailableDates = availabilityRepository.getUnavailableDates(selectedDateRange.getFrom(), selectedDateRange.getTo());

        List<LocalDate> availableDates = selectedDates.stream().filter(it -> !unavailableDates.contains(it)).collect(Collectors.toList());

        return new AvailabilityResponse(selectedDateRange, availableDates);
    }

    private SelectedDateRange createDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            LOGGER.info("No availability selected, using default values to search for availability. 1 day ahead arrival and 30 days in advance");
            from = LocalDate.now().plusDays(1);
            to = from.plusDays(30);
        }
        return new SelectedDateRange(from, to);
    }
}
