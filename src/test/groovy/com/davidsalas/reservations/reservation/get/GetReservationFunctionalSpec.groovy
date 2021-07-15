package com.davidsalas.reservations.reservation.get

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import com.davidsalas.reservations.util.ErrorMessageConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class GetReservationFunctionalSpec extends FunctionalTestConfiguration {

    MockMvc mockMvc

    @Autowired
    ReservationController reservationController

    @Autowired
    GlobalControllerAdvice customExceptionHandler

    @Autowired
    ReservationRepository reservationRepository

    @Autowired
    AvailabilityRepository availabilityRepository

    void setup() {

        mockMvc = standaloneSetup(reservationController)
                .setControllerAdvice(customExceptionHandler)
                .build()
    }

    def "given an existing reservation, when received the if of that reservation then return the data of it"() {
        given:("A valid reservation request")
        /*
        The reservation id is persisted in test database at application initialization from model.sql on resources folder
         */
        def reservation = reservationRepository.findById(10002L).get()

        expect:
        doGetRequest(reservation.id)
                .andExpect(status().is(200))
                .andExpect(jsonPath('$.id', equalTo(Integer.valueOf(reservation.id.toString()))))
                .andExpect(jsonPath('$.full_name', equalTo(reservation.fullName)))
                .andExpect(jsonPath('$.email', equalTo(reservation.email)))
                .andExpect(jsonPath('$.arrival_date', equalTo(reservation.arrivalDate.toString())))
                .andExpect(jsonPath('$.departure_date', equalTo(reservation.departureDate.toString())))
    }

    def "given an non existing reservation, return 404"() {
        given:("An reservation request with a non existing id")
        def reservationId = 202049
        expect:
        doGetRequest(reservationId)
                .andExpect(status().is(404))
                .andExpect(jsonPath('$.error_message', equalTo(String.format(ErrorMessageConstants.RESERVATION_NOT_FOUND_ERROR_MSG, reservationId))))
    }

    def doGetRequest(Long id) {
        return mockMvc.perform(
                get("/reservations/${id}")
        )
    }
}
