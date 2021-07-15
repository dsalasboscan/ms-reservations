package com.davidsalas.reservations.util;

public class ErrorMessageConstants {
    
    // Reservation validation error messages
    public static final String INVALID_MIN_ANTICIPATION_ERROR_MSG = "Reservation should be placed with at least 1 day of anticipation";
    public static final String INVALID_MAX_ANTICIPATION_ERROR_MSG = "Reservation should be placed maximum 1 month in advance";
    public static final String EXCEEDED_MAXIMUM_STAY_ERROR_MSG = "Maximum stay on campsite is 3 days";
    public static final String UPDATE_CANCELLED_RESERVATION_ERROR_MSG = "Is not valid to update a cancelled reservation";
    public static final String RESERVATION_NOT_FOUND_ERROR_MSG = "Reservation with id: %s don't exist";

    // Availability validation error messages
    public static final String SOME_DATE_ALREADY_RESERVED_ERROR_MSG = "At least one of the dates is already reserved";

    // Date range validation error messages
    public static final String INVALID_DATE_RANGE_ERROR_MSG = "Invalid date ranges for reservation, arrival: %s departure: %s";
    public static final String INVALID_DATE_RANGE_MUST_NOT_BE_EQUAL = "Arrival and departure date can't not be equal";
    public static final String INVALID_DATE_RANGE_SHOULD_BE_IN_FUTURE = "Arrival and departure should be in the future";
}
