package guru.springframework.msscssm.services;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import java.math.BigDecimal;
import java.util.List;

import static guru.springframework.msscssm.domain.PaymentState.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService service;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Test
    void preAuth() {

        Payment savedPayment = service.newPayment(this.payment);

        StateMachine<PaymentState, PaymentEvent> stateMachine = service.preAuth(savedPayment.getId());

        assertTrue(List.of(PRE_AUTH, PRE_AUTH_ERROR).contains(stateMachine.getState().getId()));
    }

    @Test
    void auth() {

        Payment savedPayment = service.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSM = service.preAuth(savedPayment.getId());

        if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
            System.out.println("Payment is Pre Authorized");

            StateMachine<PaymentState, PaymentEvent> authSM = service.authorizePayment(savedPayment.getId());

            assertTrue(List.of(AUTH, AUTH_ERROR).contains(authSM.getState().getId()));
        } else {
            System.out.println("Payment failed pre-auth...");
        }
    }
}