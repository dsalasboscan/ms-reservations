package com.davidsalas.reservations.persistence.repository;

import com.davidsalas.reservations.persistence.entity.ReservedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<ReservedDay, Long> {

    String FIND_AVAILABILITY_BY_DATE_RANGE_QUERY = "SELECT c.day from ReservedDay c where c.day between :from and :to";

    @Query(FIND_AVAILABILITY_BY_DATE_RANGE_QUERY)
    List<LocalDate> getUnavailableDates(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
