package com.cognizant.patientService.mapper;

import com.cognizant.patientService.domain.DoctorSlot;
import com.cognizant.patientService.dto.DoctorSlotDTO;

public class DoctorSlotMapper {

    public static DoctorSlotDTO toDTO(DoctorSlot doctorSlot) {
        if (doctorSlot == null) {
            return null;
        }
        return DoctorSlotDTO.builder()
                .id(doctorSlot.getId())
                .doctorId(doctorSlot.getDoctorId())
                .slotDate(doctorSlot.getSlotDate())
                .slotTime(doctorSlot.getSlotTime())
                .booked(doctorSlot.isBooked())
                .createdAt(doctorSlot.getCreatedAt())
                .updatedAt(doctorSlot.getUpdatedAt())
                .build();
    }

    public static DoctorSlot toEntity(DoctorSlotDTO doctorSlotDTO) {
        if (doctorSlotDTO == null) {
            return null;
        }
        return DoctorSlot.builder()
                .id(doctorSlotDTO.getId())
                .doctorId(doctorSlotDTO.getDoctorId())
                .slotDate(doctorSlotDTO.getSlotDate())
                .slotTime(doctorSlotDTO.getSlotTime())
                .booked(doctorSlotDTO.isBooked())
                .build();
    }
}
