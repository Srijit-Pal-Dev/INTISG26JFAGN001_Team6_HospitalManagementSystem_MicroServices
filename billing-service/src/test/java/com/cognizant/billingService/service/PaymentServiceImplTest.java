package com.cognizant.billingService.service;

import com.cognizant.billingService.client.NotificationServiceClient;
import com.cognizant.billingService.domain.*;
import com.cognizant.billingService.dto.NotificationDTO;
import com.cognizant.billingService.dto.PaymentDTO;
import com.cognizant.billingService.exception.ResourceNotFoundException;
import com.cognizant.billingService.respository.InvoiceRepository;
import com.cognizant.billingService.respository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Invoice invoice;
    private Payment payment;

    @BeforeEach
    void setUp() {
        invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV00001")
                .patientId(1L)
                .appointmentId(10L)
                .totalAmount(new BigDecimal("500"))
                .invoiceStatus(InvoiceStatus.READY)
                .build();

        payment = Payment.builder()
                .id(1L)
                .invoice(invoice)
                .appointmentId(10L)
                .patientId(1L)
                .amount(new BigDecimal("500"))
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    // --- initiatePayment ---

    @Test
    void initiatePayment_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDTO result = paymentService.initiatePayment(1L);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void initiatePayment_InvoiceNotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.initiatePayment(99L));
    }

    @Test
    void initiatePayment_InvoiceNotReady() {
        invoice.setInvoiceStatus(InvoiceStatus.PENDING);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class, () -> paymentService.initiatePayment(1L));
    }

    @Test
    void initiatePayment_InvalidAmount_Zero() {
        invoice.setTotalAmount(BigDecimal.ZERO);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class, () -> paymentService.initiatePayment(1L));
    }

    @Test
    void initiatePayment_InvalidAmount_Null() {
        invoice.setTotalAmount(null);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class, () -> paymentService.initiatePayment(1L));
    }

    // --- updatePayment ---

    @Test
    void updatePayment_Success() {
        PaymentDTO dto = PaymentDTO.builder()
                .id(1L)
                .amount(new BigDecimal("500"))
                .paymentMethod(PaymentMethod.UPI)
                .build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDTO result = paymentService.updatePayment(dto);

        assertNotNull(result);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void updatePayment_NotFound() {
        PaymentDTO dto = PaymentDTO.builder().id(99L).build();
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.updatePayment(dto));
    }

    // --- confirmPayment ---

    @Test
    void confirmPayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            return p;
        });

        PaymentDTO result = paymentService.confirmPayment(1L, 1L, PaymentMethod.CARD);

        assertNotNull(result);
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        assertNotNull(payment.getTransactionId());
        assertEquals(PaymentMethod.CARD, payment.getPaymentMethod());
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void confirmPayment_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.confirmPayment(1L, 99L, PaymentMethod.CARD));
    }

    // --- getPaymentById ---

    @Test
    void getPaymentById_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPaymentById_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    // --- getAllPaymenta ---

    @Test
    void getAllPaymenta_Success() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentDTO> result = paymentService.getAllPaymenta();

        assertEquals(1, result.size());
    }

    @Test
    void getAllPaymenta_Empty() {
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        List<PaymentDTO> result = paymentService.getAllPaymenta();

        assertTrue(result.isEmpty());
    }

    // --- getPaymentsByPatientId ---

    @Test
    void getPaymentsByPatientId_Success() {
        when(paymentRepository.findByPatientId(1L)).thenReturn(List.of(payment));

        List<PaymentDTO> result = paymentService.getPaymentsByPatientId(1L);

        assertEquals(1, result.size());
    }

    // --- getPaymentByInvoiceId ---

    @Test
    void getPaymentByInvoiceId_Success() {
        when(paymentRepository.findByInvoiceId(1L)).thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentByInvoiceId(1L);

        assertNotNull(result);
    }

    @Test
    void getPaymentByInvoiceId_NotFound() {
        when(paymentRepository.findByInvoiceId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByInvoiceId(99L));
    }

    // --- cancelPayment ---

    @Test
    void cancelPayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDTO result = paymentService.cancelPayment(1L);

        assertNotNull(result);
        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void cancelPayment_NotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.cancelPayment(99L));
    }

    // --- getPaymentsByStatus ---

    @Test
    void getPaymentsByStatus_Success() {
        when(paymentRepository.findByPaymentStatus(PaymentStatus.PENDING)).thenReturn(List.of(payment));

        List<PaymentDTO> result = paymentService.getPaymentsByStatus(PaymentStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void getPaymentsByStatus_Empty() {
        when(paymentRepository.findByPaymentStatus(PaymentStatus.FAILED)).thenReturn(Collections.emptyList());

        List<PaymentDTO> result = paymentService.getPaymentsByStatus(PaymentStatus.FAILED);

        assertTrue(result.isEmpty());
    }
}

