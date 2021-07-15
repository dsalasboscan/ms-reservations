package com.davidsalas.reservations.reservation.update

import com.davidsalas.reservations.FunctionalTestConfiguration
import com.davidsalas.reservations.controller.ReservationController
import com.davidsalas.reservations.exception.handler.GlobalControllerAdvice
import com.davidsalas.reservations.model.enums.ReservationStatusEnum
import com.davidsalas.reservations.model.request.UpdateReservationRequest
import com.davidsalas.reservations.persistence.entity.Reservation
import com.davidsalas.reservations.persistence.entity.ReservedDay
import com.davidsalas.reservations.persistence.repository.AvailabilityRepository
import com.davidsalas.reservations.persistence.repository.ReservationRepository
import com.davidsalas.reservations.util.DateRangeGenerator
import com.davidsalas.reservations.util.ErrorMessageConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import java.time.LocalDate
import java.util.stream.Collectors

import static com.davidsalas.reservations.util.StringSingleLineConvertor.singleLine
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class UpdateReservationFunctionalSpec extends FunctionalTestConfiguration {

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

    def "given an existing reservation update the fields"() {
        given:("An existing reservation that needs to be updated")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_EIGHT_TO_DAY_TEN)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()
        def reservation = createReservation(arrivalDate, departureDate)
        def persistedReservation = reservationRepository.save(reservation)
        def reservedDays = persistedReservation.reservedDays

        and:("Another date range that is available")
        def newDateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_TWENTY_FOUR_TO_DAY_TWENTY_SEVEN)

        def request = createUpdateReservationRequest("Daniel martinez", "dani@mail.com",
                newDateRangeInfo.arrivalDate, newDateRangeInfo.departureDate)

        expect:
        doPatchRequest(persistedReservation.id, request)
                .andExpect(status().is(200))
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.full_name', equalTo(request.fullName)))
                .andExpect(jsonPath('$.email', equalTo(request.email)))
                .andExpect(jsonPath('$.arrival_date', equalTo(request.arrivalDate.toString())))
                .andExpect(jsonPath('$.departure_date', equalTo(request.departureDate.toString())))

        and:("previous reserved days are no longer reserved")
        reservedDays.stream().noneMatch ({ day -> availabilityRepository.findById(day.id).isPresent() })
    }

    def "given a cancelled reservation, then throw error because cancelled reservation cannot be updated"() {
        given:("A cancelled reservation")
        def dateRangeInfo = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_ELEVEN_TO_DAY_THIRTEEN)
        def arrivalDate = dateRangeInfo.getArrivalDate()
        def departureDate = dateRangeInfo.getDepartureDate()

        def reservation = createReservation(arrivalDate, departureDate)
        reservation.setStatus(ReservationStatusEnum.CANCELLED)
        def reservationId = reservationRepository.save(reservation).id

        def request = createUpdateReservationRequest(arrivalDate, departureDate)

        expect:
        doPatchRequest(reservationId, request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message', equalTo(ErrorMessageConstants.UPDATE_CANCELLED_RESERVATION_ERROR_MSG)))
    }

    def "given an update to reservation, when the new selected date range is invalid because some date is taken then trow error"() {
        given:("A reservation that exist that will be change the reserved days, the reserved days will be released")
        def dateRangeInfo1 = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_FOURTEEN_TO_DAY_SEVENTEEN)
        def arrivalDate1 = dateRangeInfo1.getArrivalDate()
        def departureDate1 = dateRangeInfo1.getDepartureDate()
        def reservation1 = createReservation(arrivalDate1, departureDate1)
        def reservationToUpdate = reservationRepository.save(reservation1)
        def reservedDays = reservationToUpdate.reservedDays

        and:("A reservation that exist and the update request will try to reserve it")
        def dateRangeInfo2 = DateRangeGenerator.getDateRangeInfo(DateRangeGenerator.DAY_EIGHTEEN_TO_DAY_TWENTY)
        def arrivalDate2 = dateRangeInfo2.getArrivalDate()
        def departureDate2 = dateRangeInfo2.getDepartureDate()
        def reservation2 = createReservation(arrivalDate2, departureDate2)
        reservationRepository.save(reservation2)

        def request = createUpdateReservationRequest(arrivalDate2, departureDate2)

        expect:
        doPatchRequest(reservationToUpdate.id, request)
                .andExpect(status().is(400))
                .andExpect(jsonPath('$.error_message', equalTo(ErrorMessageConstants.SOME_DATE_ALREADY_RESERVED_ERROR_MSG)))


        and:("Reserved days for reservation are kept because reservation cannot be updated")
        reservedDays.stream().allMatch ({ day -> availabilityRepository.findById(day.id).isPresent() })
    }

    def "given an unknown reservation id, then throw error with appropriate message"() {
        given:("An unknown reservation id")
        def updateReservationRequest = createUpdateReservationRequest(LocalDate.now(), LocalDate.now())

        expect:
        doPatchRequest(20202043, updateReservationRequest)
                .andExpect(status().is(404))
                .andExpect(jsonPath('$.error_message', equalTo(String.format(
                        ErrorMessageConstants.RESERVATION_NOT_FOUND_ERROR_MSG, "20202043")))
                )
    }

    def doPatchRequest(Long id, UpdateReservationRequest request) {
        def updateReservationRequestAsString = createUpdateReservationRequestAsString(request)
        return mockMvc.perform(
                patch("/reservations/${id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateReservationRequestAsString)
        )
    }

    private static String createUpdateReservationRequestAsString(UpdateReservationRequest request) {
        return singleLine(
                """
                        {
                          "email": "${request.email}",
                          "full_name": "${request.fullName}",
                          "arrival_date": "${request.arrivalDate}",
                          "departure_date": "${request.departureDate}"
                        }
                        """
        )
    }

    private static UpdateReservationRequest createUpdateReservationRequest(String fullName = "Pedro contreras", String email = "mail@mail.com",
                                                                           LocalDate arrivalDate, LocalDate departureDate) {
        return new UpdateReservationRequest(fullName, email, arrivalDate, departureDate)
    }

    private static Reservation createReservation(String fullName = "Pedro contreras", String email = "mail@mail.com", LocalDate arrivalDate, LocalDate departureDate) {
        List<ReservedDay> reservedDays = arrivalDate.datesUntil(departureDate.plusDays(1)).map({ it -> new ReservedDay(it) }).collect(Collectors.toList())
        Reservation reservation = new Reservation(fullName, email, arrivalDate, departureDate, reservedDays)
        return reservation
    }
}
