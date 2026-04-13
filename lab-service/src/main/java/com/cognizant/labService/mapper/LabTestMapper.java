package com.cognizant.labService.mapper;

import com.cognizant.labService.domain.LabResult;
import com.cognizant.labService.domain.LabTest;
import com.cognizant.labService.domain.LabTestStatus;
import com.cognizant.labService.dto.CreateLabTestRequest;
import com.cognizant.labService.dto.LabResultResponse;
import com.cognizant.labService.dto.LabTestResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class LabTestMapper {

    public Function<? super Object,?> toDto;

    // Convert Create DTO -> Entity
    public LabTest toEntity(CreateLabTestRequest dto, long count) {
        LabTest entity = new LabTest();
        entity.setPatientId(dto.getPatientId());
        entity.setAppointmentId(dto.getAppointmentId());
//        entity.setTestName(dto.getTestName());
//        entity.setFee(dto.getFee());
        entity.setStatus(LabTestStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        // Generate test code based on count
        String testCode = "T" + String.format("%03d", count + 1);
        entity.setTestCode(testCode);

        return entity;
    }

    // Convert Entity -> Response DTO
    public LabTestResponse toDto(LabTest entity) {
        return LabTestResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .appointmentId(entity.getAppointmentId())
                .testName(entity.getTestName())
                .testCode(entity.getTestCode())
                .status(entity.getStatus().name())
                .fee(entity.getFee())
                .createdAt(entity.getCreatedAt())
                .build();


    }

    // Convert LabResult Entity -> Response DTO
    public static LabResultResponse toDto(LabResult entity) {
        return LabResultResponse.builder()
                .id(entity.getId())
                .labTestId(entity.getLabTest().getId())
                .resultValue(entity.getResultValue())
                .unit(entity.getUnit())
                .referenceRange(entity.getReferenceRange())
                .isAbnormal(entity.getIsAbnormal())
                .notes(entity.getNotes())
                .recordedBy(entity.getRecordedBy())
                .recordedAt(entity.getRecordedAt())
                .build();
    }

    // Convert Response DTO -> LabResult Entity (for upload)
    public LabResult toEntity(Long labTestId, LabResultResponse dto) {
        LabResult entity = new LabResult();
        entity.setResultValue(dto.getResultValue());
        entity.setUnit(dto.getUnit());
        entity.setReferenceRange(dto.getReferenceRange());
        entity.setIsAbnormal(dto.getIsAbnormal());
        entity.setNotes(dto.getNotes());
        entity.setRecordedBy(dto.getRecordedBy());
        entity.setRecordedAt(LocalDateTime.now());
        return entity;
    }
}