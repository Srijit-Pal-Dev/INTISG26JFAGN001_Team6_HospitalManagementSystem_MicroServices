package com.cognizant.billingService.controller;

import com.cognizant.billingService.domain.MediclaimStatus;
import com.cognizant.billingService.dto.MediclaimDTO;
import com.cognizant.billingService.exception.InvalidRoleException;
import com.cognizant.billingService.service.MediclaimService;
import com.cognizant.billingService.util.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mediclaim")
public class MediclaimController {

	private final MediclaimService mediclaimService;

	public MediclaimController(MediclaimService mediclaimService) {
		this.mediclaimService = mediclaimService;
	}

	@PostMapping("/process")
	public ResponseEntity<ApiResponse<MediclaimDTO>> processMediclaim(
		@RequestHeader("X-User-Role") String roles,
		@Valid @RequestBody MediclaimDTO mediclaimDTO
	) {
		if (!roles.contains("USER") && !roles.contains("ADMIN")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		MediclaimDTO mediClaim = mediclaimService.createMediclaim(mediclaimDTO);
		if (mediClaim != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaim Processed Successfully", mediClaim));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to process Mediclaim", null));
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<MediclaimDTO>> updateMediclaim(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id,
		@RequestParam MediclaimStatus status
	) {
		if (!roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		MediclaimDTO mediClaim = mediclaimService.updateMediclaimStatus(id, status);
		if (mediClaim != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaim Updated Successfully", mediClaim));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update Mediclaim", null));
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<MediclaimDTO>> getMediclaimById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("ADMIN") && !roles.contains("USER") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		MediclaimDTO mediclaimDTO = mediclaimService.getMediclaimById(id);
		if (mediclaimDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaim Retrieved Successfully", mediclaimDTO));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Mediclaim not found with id: " + id, null));
		}
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<MediclaimDTO>>> getMediclaimByPatientId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long patientId
	) {
		if (!roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<MediclaimDTO> mediclaims = mediclaimService.getAllMediclaimsByPatientId(patientId);
		if (mediclaims != null && !mediclaims.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaims Retrieved Successfully", mediclaims));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Mediclaims not found for patient id: " + patientId, null));
		}  
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<MediclaimDTO>>> getAllMediclaims(
		@RequestHeader("X-User-Role") String roles
	) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<MediclaimDTO> mediclaims = mediclaimService.getAllMediclaims();
		if (mediclaims != null && !mediclaims.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaims Retrieved Successfully", mediclaims));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "No Mediclaims found", null));
		}
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<ApiResponse<List<MediclaimDTO>>> getMediclaimsByStatus(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable MediclaimStatus status
	) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<MediclaimDTO> mediclaims = mediclaimService.getMediclaimsByStatus(status);
		if (mediclaims != null && !mediclaims.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Mediclaims Retrieved Successfully", mediclaims));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "No Mediclaims found with status: " + status, null));
		}
	}
}
