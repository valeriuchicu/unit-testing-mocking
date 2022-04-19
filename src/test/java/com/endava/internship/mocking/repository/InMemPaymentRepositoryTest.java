package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class InMemPaymentRepositoryTest {

    PaymentRepository paymentRepository;

    Payment payment;

    Payment payment1;

    Payment payment2;

    @BeforeEach
    void setUp() {
        paymentRepository = new InMemPaymentRepository();
        payment = new Payment(33, 555.00, "Insert amount");
        payment1 = new Payment(44, 666.00, "Insert amount");
        payment2 = new Payment(55, 777.00, "Insert amount");
        paymentRepository.save(payment);
        paymentRepository.save(payment1);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfTheUUIDIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentRepository.findById(null))
                .withMessage("Payment id must not be null");
    }

    @Test
    void shouldReturnPaymentById() {
        assertEquals(Optional.of(payment), paymentRepository.findById(payment.getPaymentId()));
    }

    @Test
    void shouldReturnAllPayments() {
        assertThat(paymentRepository.findAll()).containsExactlyInAnyOrder(payment, payment1);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfThePaymentIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentRepository.save(null))
                .withMessage("Payment must not be null");

    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfThePaymentIsAlreadySaved() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentRepository.save(payment))
                .withMessage("Payment with id " + payment.getPaymentId() + " already saved");
    }

    @Test
    void shouldReturnSavedPayment() {
        assertEquals(payment2, paymentRepository.save(payment2));
    }

    @Test
    void shouldThrowNoSuchElementExceptionIfThePaymentIsNull() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> paymentRepository.editMessage(payment2.getPaymentId(), "The payment was canceled"))
                .withMessage("Payment with id " + payment2.getPaymentId() + " not found");

    }

    @Test
    void shouldSetNewMessage() {
        String expectedMessage = "The payment was canceled";
        Payment editedPayment = paymentRepository.editMessage(payment.getPaymentId(), "The payment was canceled");

        assertEquals(expectedMessage, editedPayment.getMessage());
    }
}