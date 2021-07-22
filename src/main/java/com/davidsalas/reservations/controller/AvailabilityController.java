package com.davidsalas.reservations.controller;

import com.davidsalas.reservations.model.response.AvailabilityResponse;
import com.davidsalas.reservations.service.AvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityController.class);

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AvailabilityResponse checkCampsiteAvailability(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        AvailabilityResponse availabilityResponse = availabilityService.checkAvailability(from, to);
        LOGGER.info("Finish check of availability of campsite with result {}", availabilityResponse);
        return availabilityResponse;
    }
}
