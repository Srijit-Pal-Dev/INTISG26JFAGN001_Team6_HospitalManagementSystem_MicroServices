package com.cognizant.patientService.controller;

import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.dto.DoctorSlotDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.DoctorSlotService;
import com.cognizant.patientService.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor-slot")
@Tag(name = "Doctor Slot API", description = "API for managing doctor slots")
public class DoctorSlotController {

	private final DoctorSlotService doctorSlotService;

	public DoctorSlotController(DoctorSlotService doctorSlotService) {
		this.doctorSlotService = doctorSlotService;
	}

	@Operation(
		summary = "Create a doctor slot",
		description = "Create a new doctor slot. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slot Created Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = DoctorSlotDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Create Slot Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slot Created Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"AVAILABLE\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to create Doctor Slot",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Failed Create Slot Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to create Doctor Slot\",\n" +
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
							name = "Forbidden Create Slot Response",
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

	@Operation(
		summary = "Create multiple doctor slots",
		description = "Create multiple doctor slots in bulk. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slots Created Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Create Many Slots Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slots Created Successfully\",\n" +
							"  \"data\": [\n" +
							"    {\n" +
							"      \"id\": 1,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    },\n" +
							"    {\n" +
							"      \"id\": 2,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:30:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    }\n" +
							"  ]\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to create Doctor Slots",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Failed Create Many Slots Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to create Doctor Slots\",\n" +
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
							name = "Forbidden Create Many Slots Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"\"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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

	@Operation(
		summary = "Update a doctor slot",
		description = "Update an existing doctor slot. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slot Updated Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = DoctorSlotDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Update Slot Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slot Updated Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"AVAILABLE\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to update Doctor Slot",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Failed Update Slot Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to update Doctor Slot\",\n" +
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
							name = "Forbidden Update Slot Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Doctor Slot not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Update Slot Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Doctor Slot not found\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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

	@Operation(
		summary = "Get a doctor slot by ID",
		description = "Retrieve a doctor slot by its ID. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST, USER"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slot Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = DoctorSlotDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get Slot Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slot Found\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"doctorId\": 456,\n" +
							"    \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"    \"status\": \"AVAILABLE\"\n" +
							"  }\n" +
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
							name = "Forbidden Get Slot Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"\"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Doctor Slot not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get Slot Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Doctor Slot not found\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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

	@Operation(
		summary = "Get doctor slots by doctor ID",
		description = "Retrieve all doctor slots for a specific doctor. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST, USER"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slots Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Get Slots By Doctor ID Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slots Found\",\n" +
							"  \"data\": [\n" +
							"    {\n" +
							"      \"id\": 1,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    },\n" +
							"    {\n" +
							"      \"id\": 2,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:30:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    }\n" +
							"  ]\n" +
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
							name = "Forbidden Get Slots By Doctor ID Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"\"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Doctor Slots not found for doctor id",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get Slots By Doctor ID Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Doctor Slots not found for doctor id 456\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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

	@Operation(
		summary = "Get all doctor slots",
		description = "Retrieve all doctor slots. Allowed roles: ADMIN, RECEPTIONIST"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slots Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Get All Slots Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slots Found\",\n" +
							"  \"data\": [\n" +
							"    {\n" +
							"      \"id\": 1,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:00:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    },\n" +
							"    {\n" +
							"      \"id\": 2,\n" +
							"      \"doctorId\": 456,\n" +
							"      \"appointmentDateTime\": \"2024-07-01T10:30:00\",\n" +
							"      \"status\": \"AVAILABLE\"\n" +
							"    }\n" +
							"  ]\n" +
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
							name = "Forbidden Get All Slots Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"\"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Doctor Slots not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Get All Slots Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Doctor Slots not found\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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

	@Operation(
		summary = "Delete a doctor slot",
		description = "Delete an existing doctor slot. Allowed roles: DOCTOR, ADMIN, RECEPTIONIST"
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Doctor Slot Deleted Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Delete Slot Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Doctor Slot Deleted Successfully\",\n" +
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
							name = "Forbidden Delete Slot Response",
							value = "{\n" +
							"  \"status\": 403,\n" +
							"  \"message\": \"Forbidden: You don't have permission to access this resource\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Doctor Slot not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Not Found Delete Slot Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Doctor Slot not found\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
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
