package com.davidsalas.reservations.unit

import com.davidsalas.reservations.controller.AvailabilityController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.service.AvailabilityService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class AvailabilityServiceSpec extends Specification {

    MockMvc mockMvc

    @Autowired
    AvailabilityController availabilityController

    @Autowired
    AvailabilityService availabilityService

    @Autowired
    GlobalControllerAdvice customExceptionHandler

    @SpringBean
    AvailabilityRepository availabilityRepository = Mock(AvailabilityRepository)


    void setup() {
        mockMvc = standaloneSetup(availabilityController)
                .setControllerAdvice(customExceptionHandler)
                .build()
    }

    def "given an availability request without selected dates, when some dates are reserved, don't return those dates"() {
        given:
        def currentDate = LocalDate.now()
        def unavailableDates = List.of(currentDate.plusDays(5),  currentDate.plusDays(6), currentDate.plusDays(7),
                currentDate.plusDays(10), currentDate.plusDays(11), currentDate.plusDays(12))

        when:("database return a list of unavailable days")
        availabilityRepository.getUnavailableDates(_, _) >> unavailableDates

        and:("Availability get process is called")
        def response = availabilityService.checkAvailability(null, null)

        then:("Any of the unavailable dates are in the response o available dates")
        response.availableDates.stream().allMatch({ it -> !unavailableDates.contains(it) })

        and:("The number of available dates is the default date range (30 days) without the 6 unavailable dates")
        response.availableDates.size() == 26
    }
}
