package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class InMemPaymentRepositoryTest {

    PaymentRepository inMemPaymentRepository;
    Payment payment;
    Payment payment1;

    @BeforeEach
    void setUp(){
        inMemPaymentRepository = new InMemPaymentRepository();
        payment = new Payment(33, 555.00, "Insert amount");
        payment1 = new Payment(44, 666.00, "Insert amount");
        inMemPaymentRepository.save(payment);
        inMemPaymentRepository.save(payment1);
    }

    @Test
    void findByIdShouldThrowIllegalArgumentExceptionIfTheUUIDIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> inMemPaymentRepository.findById(null))
                .withMessage("Payment id must not be null");
    }

    @Test
    void findByIdShouldReturnPaymentById() {
        assertEquals(payment, inMemPaymentRepository.findById(payment.getPaymentId()).get());
    }

    @Test
    void findAllShouldReturnAllPayments() {
        assertThat(inMemPaymentRepository.findAll()).containsExactlyInAnyOrder(payment, payment1);
    }

    @Test
    void saveShouldThrowIllegalArgumentExceptionIfThePaymentIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> inMemPaymentRepository.save(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment must not be null");
    }
    @Test
    void saveShouldThrowIllegalArgumentExceptionIfThePaymentIsAlreadySaved() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> inMemPaymentRepository.save(payment));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment with id " + payment.getPaymentId() + "already saved");
    }
    @Test
    void saveShouldReturnSavedPayment() {
        assertEquals(payment1, inMemPaymentRepository.save(payment1));
    }

    @Test
    void editMessageShouldThrowNoSuchElementExceptionIfThePaymentIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(NoSuchElementException.class,
                () -> inMemPaymentRepository.editMessage(payment1.getPaymentId(),"The payment was canceled"));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment with id " + payment1.getPaymentId() + " not found");
    }
    @Test
    void editMessageShouldSetNewMessage() {
        Payment paymenttest = inMemPaymentRepository.editMessage(payment.getPaymentId(),"The payment was canceled");
        assertEquals("The payment was canceled", paymenttest.getMessage());
    }
}