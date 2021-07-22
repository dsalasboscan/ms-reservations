package com.davidsalas.reservations.util

import com.davidsalas.reservations.persistence.entity.ReservedDay

import java.time.LocalDate
import java.util.stream.Collectors

/*
    The database used for the functional tests is shared across tests, this class has the intention to avoid that
    tests interfere with each other, that could happen if those tests overlap on modifications to reserved days and
    violate the unique constraint that avoid two concurrent request to reserve the campsite.
 */
class DateRangeGenerator {

    // Valid scenarios
    public static final String DAY_ONE_TO_DAY_TWO = "DAY_ONE_TO_DAY_TWO"
    public static final String DAY_THREE_TO_DAY_FIVE = "DAY_THREE_TO_DAY_FIVE"
    public static final String DAY_EIGHT_TO_DAY_TEN = "DAY_EIGHT_TO_DAY_TEN"
    public static final String DAY_ELEVEN_TO_DAY_THIRTEEN = "DAY_ELEVEN_TO_DAY_THIRTEEN"
    public static final String DAY_FOURTEEN_TO_DAY_SEVENTEEN = "DAY_FOURTEEN_TO_DAY_SEVENTEEN"
    public static final String DAY_EIGHTEEN_TO_DAY_TWENTY = "DAY_EIGHTEEN_TO_DAY_TWENTY"
    public static final String DAY_TWENTY_ONE_TO_DAY_TWENTY_THREE = "DAY_TWENTY_ONE_TO_DAY_TWENTY_THREE"
    public static final String DAY_TWENTY_FOUR_TO_DAY_TWENTY_SEVEN = "DAY_TWENTY_FOUR_TO_DAY_TWENTY_SEVEN"

    // Invalid scenarios
    public static final String DATE_RANGE_WITH_MORE_THAN_3_DAYS = "DATE_RANGE_WITH_MORE_THAN_3_DAYS"
    public static final String DEPARTURE_DATE_BEFORE_ARRIVAL_DATE = "DEPARTURE_DATE_BEFORE_ARRIVAL_DATE"
    public static final String DEPARTURE_AND_ARRIVAL_DATE_RANGE_ARE_EQUAL = "DEPARTURE_AND_ARRIVAL_DATE_RANGE_ARE_EQUAL"
    public static final String ARRIVAL_DATE_WITHOUT_AT_LEAST_ONE_DAY_OF_ANTICIPATION = "ARRIVAL_DATE_WITHOUT_AT_LEAST_ONE_DAY_OF_ANTICIPATION"
    public static final String ARRIVAL_DATE_WITH_MORE_THAN_ONE_MONTH_OF_ANTICIPATION = "ARRIVAL_DATE_WITH_MORE_THAN_ONE_MONTH_OF_ANTICIPATION"

    static DateRangeInfo getDateRangeInfo(String option) {
        switch (option) {
            case DAY_ONE_TO_DAY_TWO:
                def arrivalDate = LocalDate.now().plusDays(1)
                def departureDate = arrivalDate.plusDays(1)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_THREE_TO_DAY_FIVE:
                def arrivalDate = LocalDate.now().plusDays(3)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_EIGHT_TO_DAY_TEN:
                def arrivalDate = LocalDate.now().plusDays(8)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_ELEVEN_TO_DAY_THIRTEEN:
                def arrivalDate = LocalDate.now().plusDays(11)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_FOURTEEN_TO_DAY_SEVENTEEN:
                def arrivalDate = LocalDate.now().plusDays(14)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_EIGHTEEN_TO_DAY_TWENTY:
                def arrivalDate = LocalDate.now().plusDays(18)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_TWENTY_ONE_TO_DAY_TWENTY_THREE:
                def arrivalDate = LocalDate.now().plusDays(21)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DAY_TWENTY_FOUR_TO_DAY_TWENTY_SEVEN:
                def arrivalDate = LocalDate.now().plusDays(24)
                def departureDate = arrivalDate.plusDays(2)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DATE_RANGE_WITH_MORE_THAN_3_DAYS:
                def arrivalDate = LocalDate.now().plusDays(28)
                def departureDate = arrivalDate.plusDays(3)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case DEPARTURE_DATE_BEFORE_ARRIVAL_DATE:
                def arrivalDate = LocalDate.now().plusDays(5)
                def departureDate = arrivalDate.minusDays(2)
                /* Date range is inverted, list generation is not possible, empty list is valid in this case because the date
                   range validation is the first step of the create reservation flow and the list of reserved days
                   will not be used.
                 */
                return new DateRangeInfo(arrivalDate, departureDate, new ArrayList<ReservedDay>())
                break
            case DEPARTURE_AND_ARRIVAL_DATE_RANGE_ARE_EQUAL:
                def arrivalDate = LocalDate.now().plusDays(1)
                def departureDate = LocalDate.now().plusDays(1)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case ARRIVAL_DATE_WITHOUT_AT_LEAST_ONE_DAY_OF_ANTICIPATION:
                def arrivalDate = LocalDate.now()
                def departureDate = LocalDate.now().plusDays(1)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            case ARRIVAL_DATE_WITH_MORE_THAN_ONE_MONTH_OF_ANTICIPATION:
                def arrivalDate = LocalDate.now().plusDays(40)
                def departureDate = arrivalDate.plusDays(1)
                return new DateRangeInfo(arrivalDate, departureDate, generateReservedDays(arrivalDate, departureDate))
                break
            default:
                return null
        }
    }

    private static List<ReservedDay> generateReservedDays(LocalDate arrivalDate, LocalDate departureDate) {
        return arrivalDate.datesUntil(departureDate).map({ it -> new ReservedDay(it) })
                .collect(Collectors.toList())
    }
}