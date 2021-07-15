package com.davidsalas.reservations.reservation.create

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.model.enums.ReservationStatusEnum
import com.davidsalas.reservations.model.request.CreateReservationRequest
import com.davidsalas.reservations.persistence.entity.Reservation
import com.davidsalas.reservations.persistence.entity.ReservedDay
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import com.davidsalas.reservations.util.DateRangeGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDate
import java.util.stream.Collectors

import static com.davidsalas.reservations.util.ErrorMessageConstants.SOME_DATE_ALREADY_RESERVED_ERROR_MSG
import static com.davidsalas.reservations.util.StringSingleLineConvertor.singleLine
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.hamcrest.Matchers.equalTo
import com.davidsalas.reservations.util.ErrorMessageConstants

class CreateReservationFunctionalSpec extends FunctionalTestConfiguration {

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

    def "given a valid reservation will al days available then create reservation"() {
        given:("A reservation request for valid range day 1 to day 2 of a valid data range")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_ONE_TO_DAY_TWO)

        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def reservationRequest = createReservationRequest(arrivalDate, departureDate)

        expect:
        doPostRequest(reservationRequest)
                .andExpect(status().is(201))
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.full_name', equalTo(reservationRequest.fullName)))
                .andExpect(jsonPath('$.email', equalTo(reservationRequest.email)))
                .andExpect(jsonPath('$.arrival_date', equalTo(arrivalDate.toString())))
                .andExpect(jsonPath('$.departure_date', equalTo(departureDate.toString())))
                .andExpect(jsonPath('$.status', equalTo(ReservationStatusEnum.ACTIVE.toString())))

    }

    def "given an invalid reservation with at least one of the days already reserved, then return error and don't create reservation"() {
        given:("A reservation request for valid range day 3 to day 5 of a valid data range that exist on database")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_THREE_TO_DAY_FIVE)

        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()

        reservationRepository.save(createReservation(arrivalDate, departureDate))

        def reservationRequest = createReservationRequest(arrivalDate, departureDate)

        expect:
        doPostRequest(reservationRequest)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message', equalTo(SOME_DATE_ALREADY_RESERVED_ERROR_MSG)))
    }

    def "given a reservation request, when is longer than 3 days, then return error message and don't do reservation"() {
        given:("A reservation request for a stay of more than 3 days")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DATE_RANGE_WITH_MORE_THAN_3_DAYS)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def request = createReservationRequest(arrivalDate, departureDate)

        when:
        doPostRequest(request)
            .andExpect(status().is(400))
            .andExpect(jsonPath('$.error_message', equalTo(ErrorMessageConstants.EXCEEDED_MAXIMUM_STAY_ERROR_MSG)))

        then:
        0 * reservationRepository.save(_)
    }

    def "given a reservation with invalid range, then return error message and don't do reservation"() {
        given:("A reservation request with departure date before arrival date")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DEPARTURE_DATE_BEFORE_ARRIVAL_DATE)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def request = createReservationRequest(arrivalDate, departureDate)

        when:
        doPostRequest(request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message', equalTo(String.format(
                        ErrorMessageConstants.INVALID_DATE_RANGE_ERROR_MSG, arrivalDate.toString(), departureDate.toString())))
                )

        then:
        0 * reservationRepository.save(_)
    }

    def "given a reservation with same arrival and departure date, then return error message and don't do reservation"() {
        given:("Departure and Arrival dates are equal")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DEPARTURE_AND_ARRIVAL_DATE_RANGE_ARE_EQUAL)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def request = createReservationRequest(arrivalDate, departureDate)

        when:
        doPostRequest(request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message',
                        equalTo(ErrorMessageConstants.INVALID_DATE_RANGE_MUST_NOT_BE_EQUAL))
                )

        then:
        0 * reservationRepository.save(_)
    }

    def "given a reservation without at leas 1 day of anticipation, then return error message and don't do reservation"() {
        given:("A reservation without anticipation")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.ARRIVAL_DATE_WITHOUT_AT_LEAST_ONE_DAY_OF_ANTICIPATION)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def request = createReservationRequest(arrivalDate, departureDate)

        when:
        doPostRequest(request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message',
                        equalTo(ErrorMessageConstants.INVALID_MIN_ANTICIPATION_ERROR_MSG))
                )

        then:
        0 * reservationRepository.save(_)
    }

    def "given a reservation with more than 1 month in advance, then return error message and don't do reservation"() {
        given:("A reservation with more than 1 month of anticipation")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.ARRIVAL_DATE_WITH_MORE_THAN_ONE_MONTH_OF_ANTICIPATION)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def request = createReservationRequest(arrivalDate, departureDate)

        when:
        doPostRequest(request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message',
                        equalTo(ErrorMessageConstants.INVALID_MAX_ANTICIPATION_ERROR_MSG))
                )

        then:
        0 * reservationRepository.save(_)
    }

    def doPostRequest(CreateReservationRequest createReservationRequest) {
        def requestAsString = createReservationRequestAsString(createReservationRequest)
        return mockMvc.perform(
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

    private static Reservation createReservation(LocalDate arrivalDate, LocalDate departureDate) {
        List<ReservedDay> reservedDays = arrivalDate.datesUntil(departureDate).map({ it -> new ReservedDay(it) }).collect(Collectors.toList())
        Reservation reservation = new Reservation("Pedro Contreras", "mail@mail.com", arrivalDate, departureDate, reservedDays)
        return reservation
    }
}
