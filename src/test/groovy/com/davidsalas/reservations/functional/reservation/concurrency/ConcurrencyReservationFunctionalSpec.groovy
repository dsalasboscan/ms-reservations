package com.davidsalas.reservations.functional.reservation.concurrency

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.model.request.CreateReservationRequest
import com.davidsalas.reservations.persistence.entity.Reservation
import com.davidsalas.reservations.persistence.entity.ReservedDay
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import com.davidsalas.reservations.util.DateRangeGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import static com.davidsalas.reservations.util.StringSingleLineConvertor.singleLine
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class ConcurrencyReservationFunctionalSpec extends FunctionalTestConfiguration {

    MockMvc mockMvc

    @Autowired
    ReservationController reservationController

    @Autowired
    GlobalControllerAdvice customExceptionHandler

    @Autowired
    ReservationRepository reservationRepository

    @Autowired
    AvailabilityRepository availabilityRepository

    ExecutorService executorService

    void setup() {
        executorService = Executors.newFixedThreadPool(8)
        mockMvc = standaloneSetup(reservationController)
                .setControllerAdvice(customExceptionHandler)
                .build()
    }

    @Transactional
    def "given 8 concurrent request for the same date then only create one"() {
        given:("8 reservation requests for valid range day 21 to day 23 of a valid data range")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_TWENTY_ONE_TO_DAY_TWENTY_THREE)

        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()

        def reservationRequest1 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest2 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest3 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest4 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest5 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest6 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest7 = createReservationRequest(arrivalDate, departureDate)
        def reservationRequest8 = createReservationRequest(arrivalDate, departureDate)

        List<CreateReservationRequest> reservationRequests =
                List.of(reservationRequest1, reservationRequest2, reservationRequest3, reservationRequest4,
                        reservationRequest5, reservationRequest6, reservationRequest7, reservationRequest8)

        List<Reservation> reservations = reservationRepository.findAll()

        when:("Create reservation endpoint is called 8 times")
        Future<?> futureResponse = executorService.submit(
                { -> reservationRequests.parallelStream().forEach({ it -> doPostRequest(it) }) })

        while (!futureResponse.isDone()) {
            Thread.sleep(300)
        }

        then:("Only one new reservation is persisted in db")
        reservationRepository.findAll().size() == reservations.size() + 1
    }

    def doPostRequest(CreateReservationRequest createReservationRequest) {
        def requestAsString = createReservationRequestAsString(createReservationRequest)
        mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsString)
        )
    }

    static String createReservationRequestAsString(CreateReservationRequest reservationRequest) {
        return singleLine(
                """
                        {
                          "email": "${reservationRequest.email}",
                          "full_name": "${reservationRequest.fullName}",
                          "arrival_date": "${reservationRequest.arrivalDate}",
                          "departure_date": "${reservationRequest.departureDate}"
                        }
                        """
        )
    }

    static CreateReservationRequest createReservationRequest(LocalDate arrivalDate, LocalDate departureDate) {
        return new CreateReservationRequest("mail@mail.com", "Pedro Contreras", arrivalDate, departureDate)
    }
}
