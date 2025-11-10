package com.example.MovieReservationApp.infrastructure.persistence;

import com.example.MovieReservationApp.domain.model.payment.Payment;
import com.example.MovieReservationApp.domain.model.reservation.Reservation;
import com.example.MovieReservationApp.infrastructure.persistence.repository.PaymentRepository;
import com.example.MovieReservationApp.infrastructure.persistence.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation reservation1;
    private Reservation reservation2;

    private Payment payment1;
    private Payment payment2;
    private Payment payment3;

    @BeforeEach
    void setup() {

        reservation1 = new Reservation();
        reservation2 = new Reservation();

        reservationRepository.saveAll(List.of(reservation1, reservation2));

        payment1 = Payment.builder()
                .reservation(reservation1)
                .status("PAID")
                .paidAt(OffsetDateTime.now())
                .amount(new BigDecimal("10.00"))
                .build();

        payment2 = Payment.builder()
                .reservation(reservation1)
                .status("PENDING")
                .paidAt(null)
                .amount(new BigDecimal("20.00"))
                .build();

        payment3 = Payment.builder()
                .reservation(reservation2)
                .status("FAILED")
                .paidAt(null)
                .amount(new BigDecimal("5.00"))
                .build();

        paymentRepository.saveAll(List.of(payment1, payment2, payment3));
    }

    @Test
    @DisplayName("Find payments by reservation")
    void testFindByReservation() {
        List<Payment> result = paymentRepository.findByReservation(reservation1);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Payment::getStatus)
                .containsExactlyInAnyOrder("PAID", "PENDING");
    }

    @Test
    @DisplayName("Find payments by status ignoring case")
    void testFindByStatusIgnoreCase() {
        List<Payment> result = paymentRepository.findByStatusIgnoreCase("paid");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Find payments with amount greater than")
    void testFindByAmountGreaterThan() {
        List<Payment> result = paymentRepository.findByAmountGreaterThan(new BigDecimal("10.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Find payments with amount less than")
    void testFindByAmountLessThan() {
        List<Payment> result = paymentRepository.findByAmountLessThan(new BigDecimal("10.00"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("FAILED");
    }
}
