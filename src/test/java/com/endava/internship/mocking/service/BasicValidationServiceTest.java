package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class BasicValidationServiceTest {

    ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new BasicValidationService();
    }

    @Test
    void shouldSuccessfullyValidateTheAmount() {
        assertDoesNotThrow(() -> validationService.validateAmount(55.00));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validateAmount(null))
                .withMessage("Amount must not be null");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.00, -5.00})
    void shouldThrowIllegalArgumentExceptionIfTheParameterLessOrEqualsToZero(Double amount) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validateAmount(amount))
                .withMessage("Amount must be greater than 0");
    }

    @Test
    void shouldAcceptPaymentId() {
        assertDoesNotThrow(() -> validationService.validatePaymentId(UUID.randomUUID()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfPaymentIdIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validatePaymentId(null))
                .withMessage("Payment id must not be null");
    }

    @Test
    void shouldAcceptUserId() {
        assertDoesNotThrow(() -> validationService.validateUserId(11));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfUserIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validateUserId(null))
                .withMessage("User id must not be null");
    }

    @Test
    void shouldAcceptUser() {
        assertDoesNotThrow(() -> validationService.validateUser(new User(11, "Ron", Status.ACTIVE)));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfTheUserIsInactive() {
        User user = new User(11, "Ben", Status.INACTIVE);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validateUser(user))
                .withMessage("User with id " + user.getId() + " not in ACTIVE status");
    }

    @Test
    void shouldAcceptTheMessage() {
        assertDoesNotThrow(() -> validationService.validateMessage("Payment is complete"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfMessageIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> validationService.validateMessage(null))
                .withMessage("Payment message must not be null");
    }
}
