package com.cognizant.patientService.controller;

import com.cognizant.patientService.dto.DoctorSlotDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.DoctorSlotService;
import com.cognizant.patientService.util.ApiResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor-slot")
public class DoctorSlotController {

	private final DoctorSlotService doctorSlotService;

	public DoctorSlotController(DoctorSlotService doctorSlotService) {
		this.doctorSlotService = doctorSlotService;
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<DoctorSlotDTO>> createSlot(
		@RequestHeader("X-User-Role") String roles,
		@RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody DoctorSlotDTO doctorSlotDTO
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		DoctorSlotDTO created = doctorSlotService.createSlot(doctorSlotDTO, userId);
		if (created != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slot Created Successfully", created));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to create Doctor Slot", null));
		}
	}

	@PostMapping("/create-many")
	public ResponseEntity<ApiResponse<List<DoctorSlotDTO>>> createManySlots(
		@RequestHeader("X-User-Role") String roles,
		@RequestParam Long doctorId,
		@Valid @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate slotDate,
		@Valid @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
		@RequestParam int numberOfSlots,
		@RequestParam int slotMinutes
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		List<DoctorSlotDTO> createdSlots = doctorSlotService.createManySlots(
			doctorId,
			slotDate,
			startTime,
			numberOfSlots,
			slotMinutes
		);
		if (createdSlots != null && !createdSlots.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slots Created Successfully", createdSlots));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to create Doctor Slots", null));
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<DoctorSlotDTO>> updateSlot(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id,
		@Valid @RequestBody DoctorSlotDTO doctorSlotDTO
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		DoctorSlotDTO updated = doctorSlotService.updateSlot(id, doctorSlotDTO);
		if (updated != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slot Updated Successfully", updated));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Doctor Slot not found", null));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorSlotDTO>> getSlotById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (
			!roles.contains("DOCTOR") &&
			!roles.contains("ADMIN") &&
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("USER")
		) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		DoctorSlotDTO slot = doctorSlotService.getSlotById(id);
		if (slot != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slot Found", slot));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Doctor Slot not found", null));
		}
	}

	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<List<DoctorSlotDTO>>> getSlotByDoctorId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long doctorId
	) {
		if (
			!roles.contains("DOCTOR") &&
			!roles.contains("ADMIN") &&
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("USER")
		) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		List<DoctorSlotDTO> slot = doctorSlotService.getSlotByDoctorId(doctorId);
		if (slot != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slot Found", slot));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Doctor Slot not found for doctor id " + doctorId, null));
		}
	}

	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<DoctorSlotDTO>>> getAllSlots(@RequestHeader("X-User-Role") String roles) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			return ResponseEntity
				.status(403)
				.body(new ApiResponse<>(403, "Forbidden: You don't have permission to access this resource", null));
		}
		List<DoctorSlotDTO> slots = doctorSlotService.getAllSlots();
		if (slots != null && !slots.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slots Found", slots));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Doctor Slots not found", null));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<DoctorSlotDTO>> deleteSlot(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		DoctorSlotDTO slot = doctorSlotService.getSlotById(id);
		if (slot != null) {
			doctorSlotService.deleteSlot(id);
			return ResponseEntity.ok(new ApiResponse<>(200, "Doctor Slot Deleted Successfully", null));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Doctor Slot not found", null));
		}
	}
}
