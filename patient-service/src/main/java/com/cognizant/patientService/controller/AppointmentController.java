package com.cognizant.patientService.controller;

import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.AppointmentService;
import com.cognizant.patientService.util.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<AppointmentDTO>> createAppointment(
		@RequestHeader("X-USER-Roles") String roles,
		@Valid @RequestBody AppointmentDTO appointmentDTO
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO createdAppointment = appointmentService.scheduleAppointment(appointmentDTO);
		if (createdAppointment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Scheduled Successfully", createdAppointment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to schedule appointment", null));
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> updateAppointment(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id,
		@Valid @RequestBody AppointmentDTO appointmentDTO
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
		if (updatedAppointment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Updated Successfully", updatedAppointment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update appointment", null));
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getApointmentById(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(id);
		if (appointmentDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Retrieved Successfully", appointmentDTO));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Appointment not found with id: " + id, null));
		}
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentByPatientId(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long patientId
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO appointmentDTO = appointmentService.getAppointmentByPatientId(patientId);
		if (appointmentDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Retrieved Successfully", appointmentDTO));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Appointment not found with patient id: " + patientId, null));
		}
	}

	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentByDoctorId(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long doctorId
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO appointmentDTO = appointmentService.getAppointmentByDoctorId(doctorId);
		if (appointmentDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Retrieved Successfully", appointmentDTO));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Appointment not found with doctor id: " + doctorId, null));
		}
	}

	@GetMapping("/status")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentByStatus(
		@RequestHeader("X-USER-Roles") String roles,
		@RequestParam Status status
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO appointmentDTO = appointmentService.getAppointmentByStatus(status);
		if (appointmentDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Retrieved Successfully", appointmentDTO));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Appointment not found with status: " + status, null));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAllAppointments(
		@RequestHeader("X-USER-Roles") String roles
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
		if (appointments != null && !appointments.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointments Retrieved Successfully", appointments));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "No appointments found", null));
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteAppointment(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		try {
			AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(id);
			if (appointmentDTO != null) {
				appointmentService.deleteAppointment(id);
				return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Deleted Successfully", null));
			} else {
				return ResponseEntity
					.status(404)
					.body(new ApiResponse<>(404, "Appointment not found with id: " + id, null));
			}
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, e.getMessage(), null));
		}
	}

	@PutMapping("/complete/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> completeAppointment(
		@RequestHeader("X-USER-Roles") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("ADMIN") && !roles.contains("DOCTOR")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		try {
			AppointmentDTO completedAppointment = appointmentService.completeAppointment(id);
			return ResponseEntity.ok(
				new ApiResponse<>(200, "Appointment Completed Successfully", completedAppointment)
			);
		} catch (RuntimeException e) {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, e.getMessage(), null));
		}
	}
}
