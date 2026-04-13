package com.cognizant.patientService.controller;

import com.cognizant.patientService.dto.PatientDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.PatientService;
import com.cognizant.patientService.util.ApiResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<PatientDTO>> createPatient(
		@RequestHeader("X-USER-Roles") String roles,
		@Valid @RequestBody PatientDTO patientDTO
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		System.out.println(patientDTO);
		PatientDTO created = patientService.createPatient(patientDTO);
		if (created != null) {
			return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new ApiResponse<>(201, "Patient Created Successfully", created));
		} else {
			return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ApiResponse<>(400, "Failed to create Patient", null));
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id,
		@Valid @RequestBody PatientDTO patientDTO
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PatientDTO update = patientService.updatePatient(id, patientDTO);
		if (update != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Patient Updated Successfully", update));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(400, "Patient not found", null));
		}
	}

	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatient(@RequestHeader("X-USER-Roles") String roles) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<PatientDTO> patients = patientService.getAllPatient();
		if (patients != null && !patients.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Patients Found", patients));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, "Patients not found", null));
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> getPatientById(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PatientDTO patient = patientService.getPatientById(id);
		if (patient != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Patients Found", patient));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, "Patients not found", null));
		}
	}

	@GetMapping("/mrn/{mrn}")
	public ResponseEntity<ApiResponse<PatientDTO>> getPatientByMrn(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable String mrn
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PatientDTO patient = patientService.getPatientByMrn(mrn);
		if (patient != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Patients Found", patient));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, "Patients not found", null));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> deletePatient(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PatientDTO patient = patientService.getPatientById(id);
		if (patient != null) {
			patientService.deletePatient(id);
			return ResponseEntity.ok(new ApiResponse<>(200, "Patient Deleted Successfully", null));
		} else {
			return ResponseEntity
				.status(HttpStatus.resolve(204))
				.body(new ApiResponse<>(204, "Failed to delete patient", null));
		}
	}
}
