package com.davidsalas.reservations.persistence.entity;

import com.davidsalas.reservations.model.enums.ReservationStatusEnum;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Reservation")
@Table(name = "Reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name")
    private String fullName;

    @NotBlank
    private String email;

    @NotNull
    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @NotNull
    @Column(name = "departure_date")
    private LocalDate departureDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    private List<ReservedDay> reservedDays = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Enumerated
    private ReservationStatusEnum status;

    public Reservation(String fullName, String email, LocalDate arrivalDate, LocalDate departureDate, List<ReservedDay> reservedDays) {
        this.fullName = fullName;
        this.email = email;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.reservedDays = reservedDays;
        this.status = ReservationStatusEnum.ACTIVE;
    }

    public Reservation(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public List<ReservedDay> getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(List<ReservedDay> reservedDays) {
        this.reservedDays = reservedDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public ReservationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(fullName, that.fullName) && Objects.equals(email, that.email) && Objects.equals(arrivalDate, that.arrivalDate) && Objects.equals(departureDate, that.departureDate) && Objects.equals(reservedDays, that.reservedDays) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(cancelledAt, that.cancelledAt) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, arrivalDate, departureDate, reservedDays, createdAt, updatedAt, cancelledAt, status);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", reservedDays=" + reservedDays +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", cancelledAt=" + cancelledAt +
                ", status=" + status +
                '}';
    }
}
