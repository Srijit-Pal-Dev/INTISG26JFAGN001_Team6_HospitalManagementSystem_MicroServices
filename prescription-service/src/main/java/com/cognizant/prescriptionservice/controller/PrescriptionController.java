package com.cognizant.prescriptionservice.controller;

import com.cognizant.prescriptionservice.dto.CreatePrescriptionRequest;
import com.cognizant.prescriptionservice.dto.PrescriptionResponse;
import com.cognizant.prescriptionservice.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

	private final PrescriptionService prescriptionService;

	/**
	 * Create prescription
	 * ROLE: DOCTOR
	 */
	@PostMapping("/create")
	public ResponseEntity<PrescriptionResponse> createPrescription(
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody CreatePrescriptionRequest request
	) {
		if (!role.contains("DOCTOR") && !role.contains("ADMIN")) {
			return ResponseEntity.status(403).build();
		}

		PrescriptionResponse response = prescriptionService.createPrescription(userId, request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public PrescriptionResponse getPrescriptionById(@RequestHeader("X-User-Role") String role, @PathVariable Long id) {
		return prescriptionService.getPrescriptionById(id);
	}

	@GetMapping("/appointment/{appointmentId}")
	public ResponseEntity<PrescriptionResponse> getPrescriptionByAppointmentId(
		@RequestHeader("X-User-Role") String role,
		@PathVariable Long appointmentId
	) {
		// Role validation
		if (
			!role.contains("DOCTOR") &&
			!role.contains("ADMIN") &&
			!role.contains("RECEPTIONIST") &&
			!role.contains("USER")
		) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		PrescriptionResponse response = prescriptionService.getPrescriptionByAppointmentId(appointmentId);

		return ResponseEntity.ok(response);
	}
}
