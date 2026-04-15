package com.cognizant.labService.controller;

import com.cognizant.labService.dto.CreateLabTestRequest;
import com.cognizant.labService.dto.LabResultResponse;
import com.cognizant.labService.dto.LabTestResponse;
import com.cognizant.labService.exception.InvalidRoleException;
import com.cognizant.labService.service.LabTestService;
import com.cognizant.labService.util.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lab-tests")
public class LabController {

	private final LabTestService labTestService;

	public LabController(LabTestService labTestService) {
		this.labTestService = labTestService;
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<List<LabTestResponse>>> createTest(
		@RequestHeader("X-User-Role") String roles,
		@Valid @RequestBody CreateLabTestRequest request
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException("Invalid Role: Only users with DOCTOR role can create lab tests");
		}
		List<LabTestResponse> labTest = labTestService.createLabTests(request);
		if (!labTest.isEmpty() && labTest != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Lab tests created successfully", labTest));
		} else {
			return ResponseEntity.status(500).body(new ApiResponse<>(500, "Failed to create lab test", null));
		}
	}

	@GetMapping("/pending")
	public ResponseEntity<ApiResponse<List<LabTestResponse>>> getPendingTests(
		@RequestHeader("X-User-Role") String roles
	) {
		if (!roles.contains("LAB_TECHNICIAN") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException(
				"Invalid Role: Only users with LAB_TECHNICIAN role can view pending lab tests"
			);
		}
		List<LabTestResponse> labTests = labTestService.getPendingLabTests();
		if (!labTests.isEmpty() && labTests != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Pending lab tests fetched successfully", labTests));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "No pending lab tests found", null));
		}
	}

	// COLLECT SAMPLE
	@PutMapping("/{id}/collect")
	public ResponseEntity<ApiResponse<LabTestResponse>> collectSample(
		@PathVariable Long id,
		@RequestHeader("X-User-Role") String roles
	) {
		if (!roles.contains("LAB_TECHNICIAN") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException("Invalid Role: Only users with LAB_TECHNICIAN role can collect samples");
		}
		LabTestResponse response = labTestService.collectSample(id);
		if (response != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Sample collected successfully", response));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Lab test not found with id: " + id, null));
		}
	}

	// START TEST
	@PutMapping("/{id}/start")
	public ResponseEntity<ApiResponse<LabTestResponse>> startTest(
		@PathVariable Long id,
		@RequestParam String assignedTo,
		@RequestHeader("X-User-Role") String roles
	) {
		if (!roles.contains("LAB_TECHNICIAN") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException("Invalid Role: Only users with LAB_TECHNICIAN role can start lab tests");
		}
		LabTestResponse response = labTestService.startTest(id, assignedTo);
		if (response != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Lab test started successfully", response));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Lab test not found with id: " + id, null));
		}
	}

	// UPLOAD RESULT
	@PostMapping("/{id}/result")
	public ResponseEntity<ApiResponse<LabResultResponse>> uploadResult(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id,
		@RequestBody LabResultResponse resultDto
	) {
		if (!roles.contains("LAB_TECHNICIAN") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException("Invalid Role: Only users with LAB_TECHNICIAN role can upload lab results");
		}
		LabResultResponse response = labTestService.uploadResult(id, resultDto);
		if (response != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Lab result uploaded successfully", response));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Lab test not found with id: " + id, null));
		}
	}

	// GET RESULTS BY LAB TEST ID
	@GetMapping("/{id}/results")
	public ResponseEntity<ApiResponse<LabResultResponse>> getLabResults(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Invalid Role: Only users with DOCTOR or PATIENT role can view lab results");
		}
		LabResultResponse responses = labTestService.getResultsByLabTestId(id);
		if (responses != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Lab result fetched successfully for lab test", responses));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "No lab result found for lab test with id: " + id, null));
		}
	}

	//GET RESULTS BY PATIENT ID (Patient-facing endpoint)
	@GetMapping("/patient/{patientId}/results")
	public ResponseEntity<ApiResponse<List<LabResultResponse>>> getResultsByPatientId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long patientId
	) {
		if (!roles.contains("USER") && !roles.contains("ADMIN") && !roles.contains("DOCTOR")) {
			throw new InvalidRoleException("Invalid Role: Only users with PATIENT role can view their lab results");
		}
		List<LabResultResponse> responses = labTestService.getResultsByPatientId(patientId);
		if (!responses.isEmpty() && responses != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Lab results fetched successfully for patient", responses));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "No lab results found for patient with id: " + patientId, null));
		}
	}

	@GetMapping("/appointment/tests/{appointmentId}")
	public ResponseEntity<ApiResponse<List<LabTestResponse>>> getTestsByAppointmentId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long appointmentId
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException(
				"Invalid Role: Only users with DOCTOR or RECEPTIONIST role can view lab tests for an appointment"
			);
		}
		List<LabTestResponse> responses = labTestService.getLabTestsByAppointmentId(appointmentId);
		if (!responses.isEmpty() && responses != null) {
			return ResponseEntity.ok(
				new ApiResponse<>(200, "Lab tests fetched successfully for appointment", responses)
			);
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "No lab tests found for appointment with id: " + appointmentId, null));
		}
	}
}
