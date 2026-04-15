package com.cognizant.prescriptionservice.controller;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import com.cognizant.prescriptionservice.service.DoctorService;
import com.cognizant.prescriptionservice.service.DoctorSlotService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

	private final DoctorService doctorService;
	private final DoctorSlotService doctorSlotService;

	public DoctorController(DoctorService doctorService, DoctorSlotService doctorSlotService) {
		this.doctorService = doctorService;
		this.doctorSlotService = doctorSlotService;
	}

	/**
	 * Get doctor profile
	 * Role is received via request header
	 */
	@PostMapping("/profile/create")
	public DoctorResponse createDoctorProfile(
		@RequestHeader("X-User-Role") String role,
		@RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody DoctorProfileRequest request
	) {
		if (!"DOCTOR".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
			throw new RuntimeException("Access denied: only DOCTOR role allowed");
		}
		return doctorService.createDoctorProfile(request, userId);
	}

	@GetMapping("/profile/{userId}")
	public DoctorResponse getDoctorProfile(@RequestHeader("X-User-Role") String role, @PathVariable Long userId) {
		// optional validation
		if (
			!"DOCTOR".equalsIgnoreCase(role) &&
			!"ADMIN".equalsIgnoreCase(role) &&
			!"PATIENT".equalsIgnoreCase(role) &&
			!"PHARMACIST".equalsIgnoreCase(role) &&
			!"LAB_TECHNICIAN".equalsIgnoreCase(role)
		) {
			throw new RuntimeException("Access denied: only Hospitals role allowed");
		}
		return doctorService.getDoctorProfile(userId);
	}

	/**
	 * Update doctor profile
	 */
	@PutMapping("/profile/update/{userId}")
	public DoctorResponse updateDoctorProfile(
		@RequestHeader("X-User-Role") String role,
		@PathVariable Long userId,
		@Valid @RequestBody DoctorProfileRequest request
	) {
		if (!"DOCTOR".equalsIgnoreCase(role)) {
			throw new RuntimeException("Access denied: only DOCTOR role allowed");
		}
		return doctorService.updateDoctorProfile(userId, request);
	}

	@PostMapping("/slot/create")
	public ResponseEntity<String> createSlot(
		@RequestHeader("X-User-Role") String role,
		@RequestBody DoctorSlotRequest slot
	) {
		if (!"DOCTOR".equalsIgnoreCase(role)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only DOCTOR can create slots");
		}

		doctorSlotService.addDoctorSlots(List.of(slot));
		return ResponseEntity.status(HttpStatus.CREATED).body("Doctor slot created successfully");
	}

	/**
	 * CREATE MANY SLOTS
	 */
	@PostMapping("/slot/create-many")
	public ResponseEntity<String> createManySlots(
		@RequestHeader("X-User-Role") String role,
		@RequestBody List<DoctorSlotRequest> slots
	) {
		if (!"DOCTOR".equalsIgnoreCase(role)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only DOCTOR can create slots");
		}

		doctorSlotService.addDoctorSlots(slots);
		return ResponseEntity.status(HttpStatus.CREATED).body("Doctor slots created successfully");
	}

	@GetMapping("/check/{doctorId}")
	public ResponseEntity<DoctorResponse> getDoctorById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long doctorId
	) {
        if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("DOCTOR")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		DoctorResponse doctor = doctorService.getDoctorById(doctorId);
		if (doctor != null) {
			return ResponseEntity.ok(doctor);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}
