package com.cognizant.patientService.service;

import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.DoctorSlot;
import com.cognizant.patientService.dto.DoctorSlotDTO;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.exception.SlotAlreadyExistsException;
import com.cognizant.patientService.repository.DoctorSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorSlotServiceImplTest {

    @Mock
    private DoctorSlotRepository doctorSlotRepository;

    @Mock
    private NotificationServiceClient notificationClient;

    @InjectMocks
    private DoctorSlotServiceImpl doctorSlotService;

    private DoctorSlot slot;
    private DoctorSlotDTO slotDTO;
    private final Long userId = 10L;

    @BeforeEach
    void setUp() {
        slot = DoctorSlot.builder()
                .id(1L)
                .userId(userId)
                .doctorId(5L)
                .slotDate(LocalDate.of(2026, 5, 1))
                .slotTime(LocalTime.of(10, 0))
                .booked(false)
                .build();

        slotDTO = DoctorSlotDTO.builder()
                .doctorId(5L)
                .slotDate(LocalDate.of(2026, 5, 1))
                .slotTime(LocalTime.of(10, 0))
                .booked(false)
                .build();
    }

    @Test
    void createSlot_Success() {
        when(doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(5L, slotDTO.getSlotDate(), slotDTO.getSlotTime()))
                .thenReturn(Optional.empty());
        when(doctorSlotRepository.save(any(DoctorSlot.class))).thenReturn(slot);

        DoctorSlotDTO result = doctorSlotService.createSlot(slotDTO, userId);

        assertNotNull(result);
        assertEquals(5L, result.getDoctorId());
        verify(doctorSlotRepository).save(any(DoctorSlot.class));
    }

    @Test
    void createSlot_AlreadyExists() {
        when(doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(5L, slotDTO.getSlotDate(), slotDTO.getSlotTime()))
                .thenReturn(Optional.of(slot));

        assertThrows(SlotAlreadyExistsException.class, () -> doctorSlotService.createSlot(slotDTO, userId));
    }

    @Test
    void createManySlots_Success() {
        when(doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(doctorSlotRepository.save(any(DoctorSlot.class))).thenReturn(slot);

        List<DoctorSlotDTO> result = doctorSlotService.createManySlots(
                userId, 5L, LocalDate.of(2026, 5, 1), LocalTime.of(9, 0), 3, 30);

        assertEquals(3, result.size());
        verify(doctorSlotRepository, times(3)).save(any(DoctorSlot.class));
    }

    @Test
    void createManySlots_SlotAlreadyExists() {
        when(doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(anyLong(), any(), any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(slot)); // second slot exists
        when(doctorSlotRepository.save(any(DoctorSlot.class))).thenReturn(slot);

        assertThrows(SlotAlreadyExistsException.class, () ->
                doctorSlotService.createManySlots(userId, 5L, LocalDate.of(2026, 5, 1), LocalTime.of(9, 0), 3, 30));
    }

    @Test
    void updateSlot_Success() {
        when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(doctorSlotRepository.save(any(DoctorSlot.class))).thenReturn(slot);

        DoctorSlotDTO result = doctorSlotService.updateSlot(1L, slotDTO);

        assertNotNull(result);
        verify(doctorSlotRepository).save(any(DoctorSlot.class));
    }

    @Test
    void updateSlot_NotFound() {
        when(doctorSlotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorSlotService.updateSlot(99L, slotDTO));
    }

    @Test
    void getSlotById_Success() {
        when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        DoctorSlotDTO result = doctorSlotService.getSlotById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSlotById_NotFound() {
        when(doctorSlotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorSlotService.getSlotById(99L));
    }

    @Test
    void getSlotByDoctorId_Success() {
        when(doctorSlotRepository.findByDoctorId(5L)).thenReturn(List.of(slot));

        List<DoctorSlotDTO> result = doctorSlotService.getSlotByDoctorId(5L);

        assertEquals(1, result.size());
    }

    @Test
    void getSlotByDoctorId_Empty() {
        when(doctorSlotRepository.findByDoctorId(99L)).thenReturn(Collections.emptyList());

        List<DoctorSlotDTO> result = doctorSlotService.getSlotByDoctorId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllSlots_Success() {
        when(doctorSlotRepository.findAll()).thenReturn(List.of(slot));

        List<DoctorSlotDTO> result = doctorSlotService.getAllSlots();

        assertEquals(1, result.size());
    }

    @Test
    void getAllSlots_Empty() {
        when(doctorSlotRepository.findAll()).thenReturn(Collections.emptyList());

        List<DoctorSlotDTO> result = doctorSlotService.getAllSlots();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteSlot_Success() {
        when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

        doctorSlotService.deleteSlot(1L);

        verify(doctorSlotRepository).deleteById(1L);
        verify(notificationClient).send(any(NotificationDTO.class));
    }

    @Test
    void deleteSlot_NotFound() {
        when(doctorSlotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> doctorSlotService.deleteSlot(99L));
    }
}

