package com.davidsalas.reservations.functional.availability

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.AvailabilityController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import com.davidsalas.reservations.service.AvailabilityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test_functional")
class GetAvailabilityFunctionalSpec extends FunctionalTestConfiguration {

    MockMvc mockMvc

    @Autowired
    AvailabilityController availabilityController

    @Autowired
    GlobalControllerAdvice customExceptionHandler

    @Autowired
    ReservationRepository reservationRepository

    @Autowired
    AvailabilityRepository availabilityRepository

    @Autowired
    AvailabilityService availabilityService

    void setup() {
        mockMvc = standaloneSetup(availabilityController)
                .setControllerAdvice(customExceptionHandler)
                .build()
    }

    def "given a get availability request for date rage from 2029-01-04 to 2029-01-05"() {
        given:
        def from = "2029-01-04"
        def to = "2029-01-05"

        expect:("both dates are available on response")
        doGetRequest(from, to)
            .andExpect(status().is(200))
            .andExpect(jsonPath('$.selected_date_range.from', equalTo(from)))
            .andExpect(jsonPath('$.selected_date_range.to', equalTo(to)))
            .andExpect(jsonPath('$.available_dates[0]', equalTo(from)))
            .andExpect(jsonPath('$.available_dates[1]', equalTo(to)))
    }

    def "given no selected dates on get availability request then return default date range (30 days)"() {
        expect:("both dates are available on response")
        doGetDefaultRequest()
                .andExpect(status().is(200))
    }

    def doGetRequest(String from, String to) {
        return mockMvc.perform(
                get("/availability")
                .param("from", "${from}")
                .param("to", "${to}")
        ).andDo(MockMvcResultHandlers.print())
    }

    def doGetDefaultRequest() {
        return mockMvc.perform(
                get("/availability")
        ).andDo(MockMvcResultHandlers.print())
    }
}
