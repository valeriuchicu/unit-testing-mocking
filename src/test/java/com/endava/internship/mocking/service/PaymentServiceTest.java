package com.endava.internship.mocking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ValidationService validationService;

    User user;
    Payment payment;
    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        user = new User(11, "Ben", Status.ACTIVE);
        payment = new Payment(11, 55.00, "Payed");
        paymentService = new PaymentService(userRepository, paymentRepository, validationService);
    }

    @Test
    void createPaymentShouldValidateServiceValidateUserIdAndAmount() {
        when(userRepository.findById(11)).thenReturn(Optional.ofNullable(user));
        validationService.validateUserId(11);
        validationService.validateAmount(333.00);
        validationService.validateUser(user);

        verify(validationService).validateUserId(11);
        verify(validationService).validateAmount(333.00);
        when(userRepository.findById(11)).thenReturn(Optional.ofNullable(user));
        verify(validationService).validateUser(user);

        paymentService.createPayment(11, 333.00);

        assertEquals(user, userRepository.findById(11).orElse(null));
    }

    @Test
    void createPaymentShouldThrowNoSuchElementException() {
        Throwable exceptionThatWasThrown = assertThrows(NoSuchElementException.class
                , () -> paymentService.createPayment(11, 55.00));
        assertEquals(exceptionThatWasThrown.getMessage(), "User with id " + 11 + " not found");

        Throwable exceptionThatWasThrown1 = assertThrows(NoSuchElementException.class
                , () -> paymentService.createPayment(22, 66.00));
        assertNotEquals(exceptionThatWasThrown1.getMessage(), "User with id " + 11 + " not found");
    }

    @Captor
    ArgumentCaptor<Payment> paymentArgumentCaptor;

    @Test
    void createPaymentShouldCheckUserIdAmountAndMessage() {
        when(userRepository.findById(11)).thenReturn(Optional.ofNullable(user));
        paymentService.createPayment(11, 333.00);
        verify(paymentRepository).save(paymentArgumentCaptor.capture());

        assertEquals(11, paymentArgumentCaptor.getValue().getUserId());
        assertEquals(333.00, paymentArgumentCaptor.getValue().getAmount());
        assertEquals("Payment from user Ben", paymentArgumentCaptor.getValue().getMessage());
    }

    @Test
    void editPaymentMessageShouldReturnPaymentRepository() {
        validationService.validatePaymentId(payment.getPaymentId());
        validationService.validateMessage("NEW PAYMENT");

        verify(validationService).validatePaymentId(payment.getPaymentId());
        verify(validationService).validateMessage("NEW PAYMENT");

        when(paymentRepository.editMessage(payment.getPaymentId()
                , "The message was edited")).thenReturn(payment);
        assertEquals(payment
                , paymentService.editPaymentMessage(payment.getPaymentId()
                        , "The message was edited"));
    }

    @Test
    void getAllByAmountExceeding() {
        List<Payment> paymentList = new ArrayList<>();
        paymentList.add(payment);
        paymentList.add(payment);
        paymentList.add(payment);
        when(paymentRepository.findAll()).thenReturn(paymentList);

        assertEquals(paymentList, paymentService.getAllByAmountExceeding(54.00));
        assertNotEquals(paymentList, paymentService.getAllByAmountExceeding(56.00));
    }
}
