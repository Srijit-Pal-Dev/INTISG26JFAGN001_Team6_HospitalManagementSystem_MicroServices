package com.cognizant.billingService.service;

import com.cognizant.billingService.client.*;
import com.cognizant.billingService.domain.Invoice;
import com.cognizant.billingService.domain.InvoiceStatus;
import com.cognizant.billingService.domain.Payment;
import com.cognizant.billingService.domain.PaymentStatus;
import com.cognizant.billingService.dto.*;
import com.cognizant.billingService.respository.InvoiceRepository;
import com.cognizant.billingService.respository.PaymentRepository;
import com.cognizant.billingService.util.ApiResponse;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private DoctorServiceClient doctorClient;
    @Mock
    private PatientServiceClient patientClient;
    @Mock
    private PharmacyServiceClient pharmacyClient;
    @Mock
    private LabServiceClient labClient;
    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private Invoice invoice;
    private PatientDTO patientDTO;
    private DoctorDTO doctorDTO;
    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV00001")
                .patientId(1L)
                .doctorId(5L)
                .appointmentId(10L)
                .consultationFee(new BigDecimal("500"))
                .invoiceStatus(InvoiceStatus.PENDING)
                .build();

        patientDTO = PatientDTO.builder().id(1L).fullName("John Doe").build();
        doctorDTO = DoctorDTO.builder().id(5L).fullName("Dr. Smith").consultationFee(new BigDecimal("500")).build();
        appointmentDTO = AppointmentDTO.builder().id(10L).doctorId(5L).build();
    }

    // --- initiateInvoice ---

    @Test
    void initiateInvoice_DuplicateReturnsExisting() {
        when(invoiceRepository.existsByAppointmentId(10L)).thenReturn(true);
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));

        InvoiceDTO result = invoiceService.initiateInvoice(1L, 10L);

        assertNotNull(result);
        assertEquals("INV00001", result.getInvoiceNumber());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void initiateInvoice_NoMedicinesNoLabs_ReadyImmediately() {
        when(invoiceRepository.existsByAppointmentId(10L)).thenReturn(false);
        when(patientClient.getPatientById(anyString(), eq(1L)))
                .thenReturn(new ApiResponse<>(200, "OK", patientDTO));
        when(patientClient.getAppointmentById(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", appointmentDTO));
        when(doctorClient.getDoctorById(anyString(), eq(5L))).thenReturn(doctorDTO);
        when(invoiceRepository.count()).thenReturn(0L);
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(Collections.emptyList());
        ApiResponse<List<LabDTO>> labResponse = new ApiResponse<>(200, "OK", Collections.emptyList());
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L))).thenReturn(labResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        InvoiceDTO result = invoiceService.initiateInvoice(1L, 10L);

        assertNotNull(result);
        assertEquals(InvoiceStatus.READY, result.getInvoiceStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void initiateInvoice_WithMedicines_PendingStatus() {
        when(invoiceRepository.existsByAppointmentId(10L)).thenReturn(false);
        when(patientClient.getPatientById(anyString(), eq(1L)))
                .thenReturn(new ApiResponse<>(200, "OK", patientDTO));
        when(patientClient.getAppointmentById(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", appointmentDTO));
        when(doctorClient.getDoctorById(anyString(), eq(5L))).thenReturn(doctorDTO);
        when(invoiceRepository.count()).thenReturn(0L);
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(List.of(new PharmacyDTO()));
        ApiResponse<List<LabDTO>> labResponse = new ApiResponse<>(200, "OK", Collections.emptyList());
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L))).thenReturn(labResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(1L);
            return inv;
        });

        InvoiceDTO result = invoiceService.initiateInvoice(1L, 10L);

        assertEquals(InvoiceStatus.PENDING, result.getInvoiceStatus());
    }

    // --- updateMedicineFee ---

    @Test
    void updateMedicineFee_LabFeeAlreadySet_BecomesReady() {
        invoice.setLabFee(new BigDecimal("200"));
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateMedicineFee(1L, 10L, new BigDecimal("100"), List.of());

        assertEquals(InvoiceStatus.READY, result.getInvoiceStatus());
    }

    @Test
    void updateMedicineFee_NoLabFeeNoLabTests_BecomesReady() {
        invoice.setLabFee(null);
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", Collections.emptyList()));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateMedicineFee(1L, 10L, new BigDecimal("100"), List.of());

        assertEquals(InvoiceStatus.READY, result.getInvoiceStatus());
    }

    @Test
    void updateMedicineFee_NoLabFeeButLabTestsExist_StaysPending() {
        invoice.setLabFee(null);
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", List.of(new LabDTO())));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateMedicineFee(1L, 10L, new BigDecimal("100"), List.of());

        assertEquals(InvoiceStatus.PENDING, result.getInvoiceStatus());
    }

    @Test
    void updateMedicineFee_InvoiceNotFound() {
        when(invoiceRepository.findFirstByAppointmentId(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> invoiceService.updateMedicineFee(1L, 99L, BigDecimal.TEN, List.of()));
    }

    // --- updateLabFee ---

    @Test
    void updateLabFee_MedicineFeeAlreadySet_BecomesReady() {
        invoice.setMedicineFee(new BigDecimal("100"));
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateLabFee(10L, new BigDecimal("200"), List.of());

        assertEquals(InvoiceStatus.READY, result.getInvoiceStatus());
    }

    @Test
    void updateLabFee_NoMedicineFeeNoMedicines_BecomesReady() {
        invoice.setMedicineFee(null);
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(Collections.emptyList());
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateLabFee(10L, new BigDecimal("200"), List.of());

        assertEquals(InvoiceStatus.READY, result.getInvoiceStatus());
    }

    @Test
    void updateLabFee_NoMedicineFeeMedicinesExist_StaysPending() {
        invoice.setMedicineFee(null);
        when(invoiceRepository.findFirstByAppointmentId(10L)).thenReturn(Optional.of(invoice));
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(List.of(new PharmacyDTO()));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.updateLabFee(10L, new BigDecimal("200"), List.of());

        assertEquals(InvoiceStatus.PENDING, result.getInvoiceStatus());
    }

    @Test
    void updateLabFee_InvoiceNotFound() {
        when(invoiceRepository.findFirstByAppointmentId(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> invoiceService.updateLabFee(99L, BigDecimal.TEN, List.of()));
    }

    // --- getInvoiceById ---

    @Test
    void getInvoiceById_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(patientClient.getPatientById(anyString(), eq(1L)))
                .thenReturn(new ApiResponse<>(200, "OK", patientDTO));
        when(doctorClient.getDoctorById(anyString(), eq(5L))).thenReturn(doctorDTO);
        when(patientClient.getAppointmentById(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", appointmentDTO));
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(Collections.emptyList());
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", Collections.emptyList()));

        InvoiceDTO result = invoiceService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getPatient().getFullName());
    }

    @Test
    void getInvoiceById_NotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.getInvoiceById(99L));
    }

    // --- getAllInvoices ---

    @Test
    void getAllInvoices_Success() {
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));
        when(patientClient.getPatientById(anyString(), eq(1L)))
                .thenReturn(new ApiResponse<>(200, "OK", patientDTO));
        when(doctorClient.getDoctorById(anyString(), eq(5L))).thenReturn(doctorDTO);
        when(patientClient.getAppointmentById(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", appointmentDTO));
        when(pharmacyClient.getMedicinesByAppointmentId(anyString(), eq(10L)))
                .thenReturn(Collections.emptyList());
        when(labClient.getLabTestsByAppointmentId(anyString(), eq(10L)))
                .thenReturn(new ApiResponse<>(200, "OK", Collections.emptyList()));

        List<InvoiceDTO> result = invoiceService.getAllInvoices();

        assertEquals(1, result.size());
    }

    @Test
    void getAllInvoices_Empty() {
        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());

        List<InvoiceDTO> result = invoiceService.getAllInvoices();

        assertTrue(result.isEmpty());
    }

    // --- deleteInvoice ---

    @Test
    void deleteInvoice_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        invoiceService.deleteInvoice(1L);

        verify(invoiceRepository).deleteById(1L);
    }

    @Test
    void deleteInvoice_NotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.deleteInvoice(99L));
    }

    // --- createPayemntForInvoice ---

    @Test
    void createPayemntForInvoice_Success() {
        invoice.setTotalAmount(new BigDecimal("500"));
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        InvoiceDTO result = invoiceService.createPayemntForInvoice(1L);

        assertNotNull(result);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayemntForInvoice_NotFound() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> invoiceService.createPayemntForInvoice(99L));
    }
}

