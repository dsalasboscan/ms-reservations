package com.davidsalas.reservations.availability

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import java.time.LocalDate

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class GetAvailabilityFunctionalSpec extends FunctionalTestConfiguration {

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

    def "given a get availability request for date 2029-01-04 to 2029-01-05"() {
        given:("arrival date on 2029-01-04, departure date on: 2029-01-05")
        def arrivalDate = LocalDate.parse("2029-01-04")
        def departureDate = LocalDate.parse("2029-01-05")

        expect:
        doGetRequest(arrivalDate.toString(), departureDate.toString())
            .andExpect(status().is(200))
            .andExpect(jsonPath('$.available_dates[0]', equalTo(arrivalDate.toString())))
            .andExpect(jsonPath('$.available_dates[1]', nullValue()))
    }

    def doGetRequest(String from, String to) {
        return mockMvc.perform(
                get("/availability")
                .param("from", "${from}")
                .param("to", "${to}")
        ).andDo(MockMvcResultHandlers.print())
    }
}
