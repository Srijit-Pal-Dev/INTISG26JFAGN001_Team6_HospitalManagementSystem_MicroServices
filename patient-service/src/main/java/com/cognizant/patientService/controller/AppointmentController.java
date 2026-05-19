package com.cognizant.patientService.controller;

import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.AppointmentService;
import com.cognizant.patientService.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
@Tag(name = "Appointment Controller", description = "APIs for managing appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@Operation(
		summary = "Create a new appointment",
		description = "Schedule a new appointment for a patient with a doctor"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Scheduled Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to schedule appointment",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Error Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to schedule appointment\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<AppointmentDTO>> createAppointment(
		@RequestHeader("X-User-Role") String roles,
		@RequestHeader("X-User-Id") Long userId,
		@Valid @RequestBody AppointmentDTO appointmentDTO
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO createdAppointment = appointmentService.scheduleAppointment(userId, appointmentDTO);
		if (createdAppointment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Scheduled Successfully", createdAppointment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to schedule appointment", null));
		}
	}

	@Operation(
		summary = "Update an existing appointment",
		description = "Update the details of an existing appointment"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Updated Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Update Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Updated Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T11:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to update appointment",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Error Update Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to update appointment\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Update Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> updateAppointment(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id,
		@Valid @RequestBody AppointmentDTO appointmentDTO
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("USER") &&
			!roles.contains("DOCTOR")
		) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
		if (updatedAppointment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Updated Successfully", updatedAppointment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update appointment", null));
		}
	}

	@Operation(summary = "Get appointment by ID", description = "Retrieve the details of an appointment using its ID")
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Retrieved Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Retrieved Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given ID",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with id: 1\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Get Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getApointmentById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("USER") &&
			!roles.contains("DOCTOR")
		) {
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

	@Operation(
		summary = "Get appointment by patient ID",
		description = "Retrieve the details of an appointment using the patient ID"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Retrieved Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get by Patient ID Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Retrieved Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given patient ID",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get by Patient ID Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with patient id: 123\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Get by Patient ID Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentByPatientId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long patientId
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<AppointmentDTO> appointmentDTO = appointmentService.getAppointmentByPatientId(patientId);
		if (appointmentDTO != null || !appointmentDTO.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointment Retrieved Successfully", appointmentDTO));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Appointment not found with patient id: " + patientId, null));
		}
	}

	@Operation(
		summary = "Get appointment by doctor ID",
		description = "Retrieve the details of an appointment using the doctor ID"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Retrieved Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get by Doctor ID Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Retrieved Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given doctor ID",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get by Doctor ID Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with doctor id: 456\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Get by Doctor ID Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentByDoctorId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long doctorId
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("USER") &&
			!roles.contains("DOCTOR")
		) {
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

	@Operation(
		summary = "Get appointment by status",
		description = "Retrieve the details of an appointment using its status"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Retrieved Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get by Status Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Retrieved Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"SCHEDULED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given status",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get by Status Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with status: SCHEDULED\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Get by Status Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/status")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentByStatus(
		@RequestHeader("X-User-Role") String roles,
		@RequestParam Status status
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("USER") &&
			!roles.contains("DOCTOR")
		) {
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

	@Operation(summary = "Get all appointments", description = "Retrieve the details of all appointments")
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointments Retrieved Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get All Appointments Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointments Retrieved Successfully\",\n" +
							"  \"data\": [\n" +
							"    {\n" +
							"      \"id\": 1,\n" +
							"      \"patientId\": 123,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"      \"status\": \"SCHEDULED\"\n" +
							"    },\n" +
							"    {\n" +
							"      \"id\": 2,\n" +
							"      \"patientId\": 124,\n" +
							"      \"doctorId\": 457,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T11:00:00\",\n" +
							"      \"status\": \"COMPLETED\"\n" +
							"    }\n" +
							"  ]\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "No appointments found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get All Appointments Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"No appointments found\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Get All Appointments Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAllAppointments(
		@RequestHeader("X-User-Role") String roles
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("USER") &&
			!roles.contains("DOCTOR")
		) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
		if (appointments != null && !appointments.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Appointments Retrieved Successfully", appointments));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "No appointments found", null));
		}
	}

	@Operation(summary = "Delete an appointment", description = "Delete an existing appointment using its ID")
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Deleted Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Delete Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Deleted Successfully\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given ID",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Delete Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with id: 1\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Delete Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteAppointment(
		@RequestHeader("X-User-Role") String roles,
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

	@Operation(
		summary = "Complete an appointment",
		description = "Mark an existing appointment as completed using its ID"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Appointment Completed Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = AppointmentDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Complete Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Appointment Completed Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"patientId\": 123,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"COMPLETED\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Appointment not found with the given ID",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Complete Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Appointment not found with id: 1\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "403",
				description = "Forbidden: You don't have permission to access this resource",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Forbidden Complete Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@PutMapping("/complete/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> completeAppointment(
		@RequestHeader("X-User-Role") String roles,
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
