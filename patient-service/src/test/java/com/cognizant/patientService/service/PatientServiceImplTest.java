package com.cognizant.patientService.service;

import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.dto.PatientDTO;
import com.cognizant.patientService.exception.ResourceNotFoundException;
import com.cognizant.patientService.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private PatientDTO patientDTO;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .userId(10L)
                .mrn("MRN001")
                .fullName("John Doe")
                .dob(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .bloodGroup("O+")
                .phoneNo("1234567890")
                .address("123 Main St")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        patientDTO = PatientDTO.builder()
                .id(1L)
                .userId(10L)
                .fullName("John Doe")
                .dob(LocalDate.of(1990, 1, 1))
                .gender("Male")
                .bloodGroup("O+")
                .phoneNo("1234567890")
                .address("123 Main St")
                .build();
    }

    @Test
    void createPatient_Success() {
        when(patientRepository.count()).thenReturn(0L);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("MRN001", result.getMrn());
        verify(patientRepository).save(any(Patient.class));
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void updatePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDTO result = patientService.updatePatient(1L, patientDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_NotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatient(99L, patientDTO));
    }

    @Test
    void getPatientById_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPatientById_NotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(99L));
    }

    @Test
    void getPatientByMrn_Success() {
        when(patientRepository.findPatientByMrn("MRN001")).thenReturn(Optional.of(patient));

        PatientDTO result = patientService.getPatientByMrn("MRN001");

        assertNotNull(result);
        assertEquals("MRN001", result.getMrn());
    }

    @Test
    void getPatientByMrn_NotFound() {
        when(patientRepository.findPatientByMrn("INVALID")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientByMrn("INVALID"));
    }

    @Test
    void getAllPatient_Success() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<PatientDTO> result = patientService.getAllPatient();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
    }

    @Test
    void getAllPatient_Empty() {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getAllPatient());
    }

    @Test
    void deletePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository).deleteById(1L);
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void deletePatient_NotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(99L));
    }
}

