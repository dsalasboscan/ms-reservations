package com.davidsalas.reservations.controller;

import com.davidsalas.reservations.model.request.CreateReservationRequest;
import com.davidsalas.reservations.model.request.UpdateReservationRequest;
import com.davidsalas.reservations.model.response.ReservationResponse;
import com.davidsalas.reservations.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@RequestBody @Valid CreateReservationRequest request) {
        LOGGER.info("Init of create reservation with request: {}" , request);
        ReservationResponse reservationResponse = reservationService.reserveCampsite(request);
        LOGGER.info("Finish of create reservation with response: {}" , reservationResponse);
        return reservationResponse;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponse getReservation(@PathVariable Long id) {
        LOGGER.info("Init of get reservation with id {}" , id);
        ReservationResponse reservationResponse = reservationService.searchReservation(id);
        LOGGER.info("Finish of get reservation with id {}" , id);
        return reservationResponse;
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponse updateReservation(@PathVariable Long id, @RequestBody @Valid UpdateReservationRequest request) {
        LOGGER.info("Init of update reservation with id {} and request: {}" , id, request);
        ReservationResponse reservationResponse = reservationService.updateReservation(id, request);
        LOGGER.info("Finish of update reservation with response: {}" , reservationResponse);
        return reservationResponse;
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponse deleteReservation(@PathVariable Long id) {
        LOGGER.info("Init of delete reservation with id {}", id);
        ReservationResponse reservationResponse = reservationService.deleteReservation(id);
        LOGGER.info("Finish of delete reservation with id {}", id);
        return reservationResponse;
    }
}