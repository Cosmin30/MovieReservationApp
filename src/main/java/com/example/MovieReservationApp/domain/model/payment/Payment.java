package com.example.MovieReservationApp.domain.model.payment;

import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private String status;

    private OffsetDateTime paidAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}
