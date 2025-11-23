package com.example.MovieReservationApp.domain.model.reservation;

import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.screening.Screening;
import com.example.MovieReservationApp.domain.model.ticket.Ticket;
import com.example.MovieReservationApp.domain.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "User cannot be null")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Screening cannot be null")
    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    private OffsetDateTime createdAt;

    @NotNull(message = "Status cannot be null")
    @Size(min = 1, max = 50)
    private String status;

    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be >= 0")
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;
}
