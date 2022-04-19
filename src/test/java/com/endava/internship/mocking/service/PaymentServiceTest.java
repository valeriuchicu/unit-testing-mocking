package com.endava.internship.mocking.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.mockito.Captor;
import org.mockito.InjectMocks;
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
    @Captor
    ArgumentCaptor<Payment> paymentArgumentCaptor;

    User user;
    Payment payment;
    @InjectMocks
    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        user = new User(11, "Ben", Status.ACTIVE);
        payment = new Payment(11, 55.00, "Payed");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenuserIdIsNotValid() {
        doThrow(new IllegalArgumentException("The user ID is not valid"))
                .when(validationService).validateUserId(user.getId());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentService.createPayment(11, 333.00))
                .withMessage("The user ID is not valid");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAmountIsNotValid() {
        doNothing().when(validationService).validateUserId(user.getId());
        doThrow(new IllegalArgumentException("The amount can not be negative"))
                .when(validationService).validateAmount(-50.00);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentService.createPayment(11, -50.00))
                .withMessage("The amount can not be negative");
    }

    @Test
    void shouldThrowNoSuchElementExceptionWhenUserIsNotFoundInUserRepository() {
        doNothing().when(validationService).validateUserId(22);
        doNothing().when(validationService).validateAmount(333.00);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> paymentService.createPayment(22, 333.00))
                .withMessage("User with id 22 not found");
    }

    @Test
    void shouldThrowNoSuchElementExceptionWhenUserIsNotActive() {
        doNothing().when(validationService).validateUserId(22);
        doNothing().when(validationService).validateAmount(333.00);
        when(userRepository.findById(22)).thenReturn(Optional.of(user));
        doThrow(new NoSuchElementException("This user is not active"))
                .when(validationService).validateUser(user);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> paymentService.createPayment(22, 333.00))
                .withMessage("This user is not active");
    }

    @Test
    void shouldSuccessfulCreatePayment() {
        doNothing().when(validationService).validateUserId(11);
        doNothing().when(validationService).validateAmount(333.00);
        when(userRepository.findById(11)).thenReturn(Optional.of(user));
        doNothing().when(validationService).validateUser(user);
        when(paymentRepository.save(paymentArgumentCaptor.capture())).thenReturn(payment);

        paymentService.createPayment(11, 333.00);

        assertEquals(11, paymentArgumentCaptor.getValue().getUserId());
        assertEquals(333.00, paymentArgumentCaptor.getValue().getAmount());
        assertEquals("Payment from user Ben", paymentArgumentCaptor.getValue().getMessage());

        verify(validationService).validateUserId(11);
        verify(validationService).validateAmount(333.000);
        verify(validationService).validateUser(user);
        verify(paymentRepository).save(paymentArgumentCaptor.capture());
    }

    @Test
    void shouldEditPaymentMessage() {
        Payment expectedPayment = Payment.copyOf(payment);
        expectedPayment.setMessage("NEW");

        doNothing().when(validationService).validatePaymentId(expectedPayment.getPaymentId());
        doNothing().when(validationService).validateMessage("NEW");
        when(paymentRepository.editMessage(payment.getPaymentId(), "NEW"))
                .thenReturn(expectedPayment);

        Payment actual = paymentService.editPaymentMessage(payment.getPaymentId(), "NEW");
        assertThat(expectedPayment).isEqualTo(actual);

        verify(validationService).validatePaymentId(payment.getPaymentId());
        verify(validationService).validateMessage("NEW");
        verify(paymentRepository).editMessage(payment.getPaymentId(), "NEW");
    }

    @Test
    void shouldGetAListOfWithExceedingAmount() {
        List<Payment> paymentList = new ArrayList<>();
        List<Payment> expectedPaymentList = new ArrayList<>();

        Payment payment = new Payment(11, 56.00, "Payed");
        Payment payment1 = new Payment(11, 57.00, "Payed");
        Payment payment2 = new Payment(11, 58.00, "Payed");
        Payment payment3 = new Payment(11, 59.00, "Payed");
        Payment payment4 = new Payment(11, 60.00, "Payed");

        paymentList.add(payment);
        paymentList.add(payment1);
        paymentList.add(payment2);
        paymentList.add(payment3);
        paymentList.add(payment4);

        expectedPaymentList.add(payment2);
        expectedPaymentList.add(payment3);
        expectedPaymentList.add(payment4);

        when(paymentRepository.findAll()).thenReturn(paymentList);

        assertEquals(expectedPaymentList, paymentService.getAllByAmountExceeding(57.00));
    }

    @Test
    void shouldReturnAnEmptyListWhenNoPaymentWhichExceed() {
        List<Payment> paymentList = new ArrayList<>();
        List<Payment> expectedPaymentList = new ArrayList<>();

        Payment payment = new Payment(11, 56.00, "Payed");
        Payment payment1 = new Payment(11, 57.00, "Payed");
        Payment payment2 = new Payment(11, 58.00, "Payed");
        Payment payment3 = new Payment(11, 59.00, "Payed");
        Payment payment4 = new Payment(11, 60.00, "Payed");

        paymentList.add(payment);
        paymentList.add(payment1);
        paymentList.add(payment2);
        paymentList.add(payment3);
        paymentList.add(payment4);

        when(paymentRepository.findAll()).thenReturn(paymentList);

        assertEquals(expectedPaymentList, paymentService.getAllByAmountExceeding(100.00));
    }
}
