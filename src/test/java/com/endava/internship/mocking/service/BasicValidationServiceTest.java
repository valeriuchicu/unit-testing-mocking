package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BasicValidationServiceTest {

    BasicValidationService mockBasicValidationService;
    ValidationService basicValidationService;

    @BeforeEach
    void setUp() {
        mockBasicValidationService = mock(BasicValidationService.class);
        basicValidationService = new BasicValidationService();
    }

    @Test
    void validateAmountShouldCheckSuccessOfValidationOfAmount() {
        mockBasicValidationService.validateAmount(333.00);
        verify(mockBasicValidationService, times(1)).validateAmount(333.00);
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
    void validatePaymentIdShouldCheckSuccessOfValidationOfPaymentId() {
        mockBasicValidationService.validatePaymentId(UUID.randomUUID());
        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(mockBasicValidationService, times(1)).validatePaymentId(argumentCaptor.capture());
    }

    @Test
    void validatePaymentIdShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validatePaymentId(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment id must not be null");
    }

    @Test
    void validateUserIdShouldCheckSuccessOfValidationOfUserId() {
        mockBasicValidationService.validateUserId(11);
        verify(mockBasicValidationService, times(1)).validateUserId(11);
    }

    @Test
    void validateUserIdShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUserId(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "User id must not be null");
    }

    @Test
    void validateUserShouldCheckSuccessOfValidationOfUser() {
        User user = new User(11, "Ben", Status.ACTIVE);
        mockBasicValidationService.validateUser(user);
        verify(mockBasicValidationService, times(1)).validateUser(user);
    }

    @Test
    void validateUserShouldThrowIllegalArgumentExceptionIfTheUserIsInactive() {
        User user = new User(11, "Ben", Status.INACTIVE);
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUser(user));
        assertEquals(exceptionThatWasThrown.getMessage(), "User with id " + user.getId() + " not in ACTIVE status");
    }

    @Test
    void validateMessageShouldCheckSuccessOfValidationOfMessage() {
        mockBasicValidationService.validateMessage("hello");
        verify(mockBasicValidationService, times(1)).validateMessage("hello");
    }

    @Test
    void validateMessageShouldThrowIllegalArgumentExceptionIfTheParameterIsNull() {
        Throwable exceptionThatWasThrown = assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateMessage(null));
        assertEquals(exceptionThatWasThrown.getMessage(), "Payment message must not be null");
    }
}
