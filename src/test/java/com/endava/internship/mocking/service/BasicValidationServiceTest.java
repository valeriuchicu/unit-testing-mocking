package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BasicValidationServiceTest {
    BasicValidationService basicValidationService;

    @BeforeEach
    void setUp() {
        basicValidationService = new BasicValidationService();
    }

    @Test
    void validateAmountShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Amount must not be null");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.00, -5.00})
    void validateAmountShouldThrowIllegalArgumentExceptionIfTheParameterLessOrEqualsToZero(Double amount) {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(amount));
        assertEquals(exceptionThatWasThrown.getMessage(), "Amount must be greater than 0");
    }

    @Test
    void validatePaymentIdShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validatePaymentId(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment id must not be null");
    }

    @Test
    void validateUserIdShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUserId(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "User id must not be null");
    }

    @Test
    void validateUserShouldThrowIllegalArgumentExceptionIfTheUserIsInactive() {
        User user = new User(11, "Ben", Status.INACTIVE);
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUser(user));
        assertEquals(exceptionThatWasThrown.getMessage(), "User with id " + user.getId() + " not in ACTIVE status");
    }

    @Test
    void validateMessageShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateMessage(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment message must not be null");
    }
}















