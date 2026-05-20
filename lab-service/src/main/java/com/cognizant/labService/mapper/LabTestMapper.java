package com.cognizant.labService.mapper;

import com.cognizant.labService.domain.LabResult;
import com.cognizant.labService.domain.LabTest;
import com.cognizant.labService.domain.LabTestStatus;
import com.cognizant.labService.dto.CreateLabTestRequest;
import com.cognizant.labService.dto.LabResultResponse;
import com.cognizant.labService.dto.LabTestResponse;
import java.time.LocalDateTime;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class LabTestMapper {

	public Function<? super Object, ?> toDto;

	public LabTest toEntity(CreateLabTestRequest dto, long count) {
		LabTest entity = new LabTest();
		entity.setPatientId(dto.getPatientId());
		entity.setAppointmentId(dto.getAppointmentId());
		entity.setStatus(LabTestStatus.PENDING);
		entity.setCreatedAt(LocalDateTime.now());
		String testCode = "T" + String.format("%03d", count + 1);
		entity.setTestCode(testCode);
		return entity;
	}

	public LabTestResponse toDto(LabTest entity) {
		return LabTestResponse
			.builder()
			.id(entity.getId())
			.testName(entity.getTestName())
			.patientId(entity.getPatientId())
			.appointmentId(entity.getAppointmentId())
			.testCode(entity.getTestCode())
			.status(entity.getStatus().name())
			.fee(entity.getFee())
			.createdAt(entity.getCreatedAt())
			.build();
	}

	public static LabResultResponse toDto(LabResult entity) {
		return LabResultResponse
			.builder()
			.id(entity.getId())
			.labTestId(entity.getLabTest().getId())
			.testName(entity.getTestName())
			.resultValue(entity.getResultValue())
			.unit(entity.getUnit())
			.referenceRange(entity.getReferenceRange())
			.isAbnormal(entity.getIsAbnormal())
			.notes(entity.getNotes())
			.recordedBy(entity.getRecordedBy())
			.fee(entity.getFee())
			.recordedAt(entity.getRecordedAt())
			.build();
	}

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
