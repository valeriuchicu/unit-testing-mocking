package com.endava.internship.mocking.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    void createPaymentShouldThrowNoSuchElementException() {
        when(userRepository.findById(11)).thenThrow(new NoSuchElementException("User with id 11 not found"));

        verify(validationService, never()).validateUser(user);
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> paymentService.createPayment(11, 55.00))
                .withMessage("User with id 11 not found");
    }

    @Test
    void createPaymentShouldValidateIDAmountUserAndMessage() {
        when(userRepository.findById(11)).thenReturn(Optional.of(user));
        paymentService.createPayment(11, 333.00);

        ArgumentCaptor<Integer> argumentCaptorID = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Double> argumentCaptorAmount = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<User> argumentCaptorUser = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        verify(validationService).validateUserId(argumentCaptorID.capture());
        verify(validationService).validateAmount(argumentCaptorAmount.capture());
        verify(validationService).validateUser(argumentCaptorUser.capture());
        verify(paymentRepository).save(paymentArgumentCaptor.capture());

        assertEquals(11, paymentArgumentCaptor.getValue().getUserId());
        assertEquals(333.00, paymentArgumentCaptor.getValue().getAmount());
        assertEquals("Payment from user Ben", paymentArgumentCaptor.getValue().getMessage());
    }

    @Test
    void editPaymentMessageShouldTheMEssageOfPayment() {
        paymentService.editPaymentMessage(payment.getPaymentId(), "NEW");

        ArgumentCaptor<UUID> argumentCaptorUUID = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(validationService).validateMessage(argumentCaptor.capture());
        verify(validationService).validatePaymentId(argumentCaptorUUID.capture());
        verify(paymentRepository).editMessage(argumentCaptorUUID.capture()
                , argumentCaptor.capture());
    }

    @Test
    void getAllByAmountExceeding() {
        List<Payment> actualPaymentList = new ArrayList<>();
        List<Payment> expectedPaymentList = new ArrayList<>();

        Payment payment = new Payment(11, 56.00, "Payed");
        Payment payment1 = new Payment(11, 57.00, "Payed");
        Payment payment2 = new Payment(11, 58.00, "Payed");
        Payment payment3 = new Payment(11, 59.00, "Payed");
        Payment payment4 = new Payment(11, 60.00, "Payed");

        actualPaymentList.add(payment);
        actualPaymentList.add(payment1);
        actualPaymentList.add(payment2);
        actualPaymentList.add(payment3);
        actualPaymentList.add(payment4);

        expectedPaymentList.add(payment2);
        expectedPaymentList.add(payment3);
        expectedPaymentList.add(payment4);

        when(paymentRepository.findAll()).thenReturn(actualPaymentList);

        assertEquals(expectedPaymentList, paymentService.getAllByAmountExceeding(57.00));
    }
}
