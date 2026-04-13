package com.cognizant.patientService.mapper;

import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.dto.PatientDTO;
import jakarta.validation.Valid;

public class PatientMapper {

    public static PatientDTO toDto(Patient patient){
        return PatientDTO.builder()
                .id(patient.getId())
                .userId(patient.getUserId())
                .mrn(patient.getMrn())
                .fullName(patient.getFullName())
                .dob(patient.getDob())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .phoneNo(patient.getPhoneNo())
                .address(patient.getAddress())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }

    public static Patient toEntity(PatientDTO dto){
        return Patient.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .fullName(dto.getFullName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .phoneNo(dto.getPhoneNo())
                .address(dto.getAddress())
                .build();
    }
}
