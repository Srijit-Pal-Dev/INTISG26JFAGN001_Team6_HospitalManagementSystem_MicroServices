package com.cognizant.billingService.service;

import com.cognizant.billingService.client.NotificationServiceClient;
import com.cognizant.billingService.domain.*;
import com.cognizant.billingService.dto.MediclaimDTO;
import com.cognizant.billingService.dto.NotificationDTO;
import com.cognizant.billingService.exception.ResourceNotFoundException;
import com.cognizant.billingService.respository.MediclaimRepository;
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
class MediclaimServiceImplTest {

    @Mock
    private MediclaimRepository mediclaimRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private MediclaimServiceImpl mediclaimService;

    private Payment payment;
    private Invoice invoice;
    private Mediclaim mediclaim;

    @BeforeEach
    void setUp() {
        invoice = Invoice.builder()
                .id(1L)
                .patientId(1L)
                .totalAmount(new BigDecimal("1000"))
                .build();

        payment = Payment.builder()
                .id(1L)
                .invoice(invoice)
                .transactionId("TXN123")
                .patientId(1L)
                .build();

        mediclaim = Mediclaim.builder()
                .id(1L)
                .patientId(1L)
                .invoiceId(1L)
                .paymentId(1L)
                .policyNumber("POL001")
                .insurerName("HealthInsure")
                .coveragePercentage(new BigDecimal("80"))
                .refundAmount(new BigDecimal("800"))
                .status(MediclaimStatus.PENDING)
                .build();
    }

    // --- createMediclaim ---

    @Test
    void createMediclaim_Success() {
        MediclaimDTO dto = MediclaimDTO.builder()
                .paymentId(1L)
                .policyNumber("POL001")
                .insurerName("HealthInsure")
                .coveragePercentage(new BigDecimal("80"))
                .build();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(mediclaimRepository.save(any(Mediclaim.class))).thenReturn(mediclaim);

        MediclaimDTO result = mediclaimService.createMediclaim(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("800"), result.getRefundAmount());
        assertEquals(MediclaimStatus.PENDING, result.getStatus());
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void createMediclaim_PaymentNotFound() {
        MediclaimDTO dto = MediclaimDTO.builder().paymentId(99L).build();
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediclaimService.createMediclaim(dto));
    }

    @Test
    void createMediclaim_PaymentNotCompleted() {
        payment.setTransactionId(null);
        MediclaimDTO dto = MediclaimDTO.builder().paymentId(1L).build();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () -> mediclaimService.createMediclaim(dto));
    }

    // --- updateMediclaimStatus ---

    @Test
    void updateMediclaimStatus_Approved() {
        when(mediclaimRepository.findById(1L)).thenReturn(Optional.of(mediclaim));
        when(mediclaimRepository.save(any(Mediclaim.class))).thenReturn(mediclaim);

        MediclaimDTO result = mediclaimService.updateMediclaimStatus(1L, MediclaimStatus.APPROVED);

        assertNotNull(result);
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void updateMediclaimStatus_Rejected() {
        when(mediclaimRepository.findById(1L)).thenReturn(Optional.of(mediclaim));
        when(mediclaimRepository.save(any(Mediclaim.class))).thenReturn(mediclaim);

        MediclaimDTO result = mediclaimService.updateMediclaimStatus(1L, MediclaimStatus.REJECTED);

        assertNotNull(result);
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void updateMediclaimStatus_Pending_NoNotification() {
        when(mediclaimRepository.findById(1L)).thenReturn(Optional.of(mediclaim));
        when(mediclaimRepository.save(any(Mediclaim.class))).thenReturn(mediclaim);

        MediclaimDTO result = mediclaimService.updateMediclaimStatus(1L, MediclaimStatus.PENDING);

        assertNotNull(result);
        verify(notificationClient, never()).send(any(NotificationDTO.class));
    }

    @Test
    void updateMediclaimStatus_NotFound() {
        when(mediclaimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> mediclaimService.updateMediclaimStatus(99L, MediclaimStatus.APPROVED));
    }

    // --- getMediclaimById ---

    @Test
    void getMediclaimById_Success() {
        when(mediclaimRepository.findById(1L)).thenReturn(Optional.of(mediclaim));

        MediclaimDTO result = mediclaimService.getMediclaimById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getMediclaimById_NotFound() {
        when(mediclaimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mediclaimService.getMediclaimById(99L));
    }

    // --- getAllMediclaimsByPatientId ---

    @Test
    void getAllMediclaimsByPatientId_Success() {
        when(mediclaimRepository.findByPatientId(1L)).thenReturn(List.of(mediclaim));

        List<MediclaimDTO> result = mediclaimService.getAllMediclaimsByPatientId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAllMediclaimsByPatientId_Empty() {
        when(mediclaimRepository.findByPatientId(99L)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> mediclaimService.getAllMediclaimsByPatientId(99L));
    }

    // --- getAllMediclaims ---

    @Test
    void getAllMediclaims_Success() {
        when(mediclaimRepository.findAll()).thenReturn(List.of(mediclaim));

        List<MediclaimDTO> result = mediclaimService.getAllMediclaims();

        assertEquals(1, result.size());
    }

    @Test
    void getAllMediclaims_Empty() {
        when(mediclaimRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> mediclaimService.getAllMediclaims());
    }

    // --- getMediclaimsByStatus ---

    @Test
    void getMediclaimsByStatus_Success() {
        when(mediclaimRepository.findByStatus(MediclaimStatus.PENDING)).thenReturn(List.of(mediclaim));

        List<MediclaimDTO> result = mediclaimService.getMediclaimsByStatus(MediclaimStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void getMediclaimsByStatus_Empty() {
        when(mediclaimRepository.findByStatus(MediclaimStatus.APPROVED)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> mediclaimService.getMediclaimsByStatus(MediclaimStatus.APPROVED));
    }
}

