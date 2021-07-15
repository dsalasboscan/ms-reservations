package com.davidsalas.reservations.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "ReservedDay")
@Table(name = "ReservedDays")
public class ReservedDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day", unique = true)
    private LocalDate day;

    public ReservedDay(LocalDate day) {
        this.day = day;
    }

    public ReservedDay(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservedDay that = (ReservedDay) o;
        return Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "ReservedDay{" +
                "id=" + id +
                ", day=" + day +
                '}';
    }
}
