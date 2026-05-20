package com.cognizant.labService.service;

import com.cognizant.labService.domain.LabResult;
import com.cognizant.labService.domain.LabTest;
import com.cognizant.labService.dto.CreateLabTestRequest;
import com.cognizant.labService.dto.LabResultResponse;
import com.cognizant.labService.dto.LabTestResponse;
import java.util.List;
import java.util.Optional;

public interface LabTestService {
	// CREATE LAB TEST
	List<LabTestResponse> createLabTests(CreateLabTestRequest request);
    List<LabTestResponse> getAllLabTests();
	List<LabTestResponse> getPendingLabTests();
	LabTestResponse collectSample(Long labTestId);
	LabTestResponse startTest(Long labTestId, String assignedTo, Long userId);
	LabResultResponse uploadResult(Long userId, Long labTestId, LabResultResponse resultDto);
	LabResultResponse getResultsByLabTestId(Long labTestId);
	List<LabResultResponse> getResultsByPatientId(Long patientId);

	List<LabTestResponse> getLabTestsByAppointmentId(Long appointmentId);
}
