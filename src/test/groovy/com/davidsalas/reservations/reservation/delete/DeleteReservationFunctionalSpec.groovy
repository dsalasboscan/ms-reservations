package com.davidsalas.reservations.reservation.delete

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.model.enums.ReservationStatusEnum
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class DeleteReservationFunctionalSpec extends FunctionalTestConfiguration {

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

    def "Given a request to delete an existing reservation, then make the logical delete of the reservation and release availab"() {
        given:("An existing reservation id to be deleted")
        /*
        The reservation id is persisted in test database at application initialization from model.sql on resources folder
         */
        def reservation = reservationRepository.findById(10001L).get()

        when:
        doDeleteRequest(reservation.id)
                .andExpect(status().is(200))
                .andExpect(jsonPath('$.id', equalTo(Integer.valueOf(reservation.id.toString()))))
                .andExpect(jsonPath('$.full_name', equalTo(reservation.fullName)))
                .andExpect(jsonPath('$.email', equalTo(reservation.email)))
                .andExpect(jsonPath('$.arrival_date', equalTo(reservation.arrivalDate.toString())))
                .andExpect(jsonPath('$.departure_date', equalTo(reservation.departureDate.toString())))
                .andExpect(jsonPath('$.status', equalTo(ReservationStatusEnum.CANCELLED.toString())))

        then:
        !availabilityRepository.findById(100001L).isPresent()
        !availabilityRepository.findById(100002L).isPresent()
    }

    def doDeleteRequest(Long id) {
        return mockMvc.perform(
                delete("/reservations/${id}")
        )
    }
}
