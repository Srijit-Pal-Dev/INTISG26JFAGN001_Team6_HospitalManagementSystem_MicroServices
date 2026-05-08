package com.cognizant.patientService.controller;

import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.dto.DoctorSlotDTO;
import com.cognizant.patientService.dto.PatientDTO;
import com.cognizant.patientService.exception.InvalidRoleException;
import com.cognizant.patientService.service.PatientService;
import com.cognizant.patientService.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
@Tag(name = "Patient API", description = "API for managing patients")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	@Operation(
		summary = "Create a new patient",
		description = "Creates a new patient record in the system. Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "201",
				description = "Patient Created Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PatientDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Create Patient Response",
							value = "{\n" +
							"  \"status\": 201,\n" +
							"  \"message\": \"Patient Created Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"name\": \"John Doe\",\n" +
							"    \"mrn\": \"MRN123456\",\n" +
							"    \"age\": 30,\n" +
							"    \"gender\": \"Male\",\n" +
							"    \"contactNumber\": \"123-456-7890\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Failed to create Patient",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Failed Create Patient Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Failed to create Patient\",\n" +
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
	public ResponseEntity<ApiResponse<PatientDTO>> createPatient(
		@RequestHeader("X-User-Role") String roles,
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

	@Operation(
		summary = "Update an existing patient",
		description = "Updates an existing patient record in the system. Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Patient Updated Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PatientDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Update Patient Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Patient Updated Successfully\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"name\": \"John Doe\",\n" +
							"    \"mrn\": \"MRN123456\",\n" +
							"    \"age\": 30,\n" +
							"    \"gender\": \"Male\",\n" +
							"    \"contactNumber\": \"123-456-7890\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "400",
				description = "Patient not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Patient Not Found Response",
							value = "{\n" +
							"  \"status\": 400,\n" +
							"  \"message\": \"Patient not found\",\n" +
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
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(
		@RequestHeader("X-User-Role") String roles,
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

	@Operation(
		summary = "Get all patients",
		description = "Retrieves a list of all patients in the system. Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Patients Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PatientDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get All Patients Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Patients Found\",\n" +
							"  \"data\": [\n" +
							"    {\n" +
							"      \"id\": 1,\n" +
							"      \"name\": \"John Doe\",\n" +
							"      \"mrn\": \"MRN123456\",\n" +
							"      \"age\": 30,\n" +
							"      \"gender\": \"Male\",\n" +
							"      \"contactNumber\": \"123-456-7890\"\n" +
							"    },\n" +
							"    {\n" +
							"      \"id\": 2,\n" +
							"      \"name\": \"Jane Smith\",\n" +
							"      \"mrn\": \"MRN654321\",\n" +
							"      \"age\": 25,\n" +
							"      \"gender\": \"Female\",\n" +
							"      \"contactNumber\": \"987-654-3210\"\n" +
							"    }\n" +
							"  ]\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Patients not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Patients Not Found Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Patients not found\",\n" +
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
							"\"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@GetMapping("/")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatient(@RequestHeader("X-User-Role") String roles) {
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

	@Operation(
		summary = "Get patient by ID",
		description = "Retrieves a patient record by its unique ID. Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Patient Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PatientDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get Patient By ID Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Patient Found\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"name\": \"John Doe\",\n" +
							"    \"mrn\": \"MRN123456\",\n" +
							"    \"age\": 30,\n" +
							"    \"gender\": \"Male\",\n" +
							"    \"contactNumber\": \"123-456-7890\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Patient not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Patient Not Found Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Patient not found\",\n" +
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
	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> getPatientById(
		@RequestHeader("X-User-Role") String roles,
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

	@Operation(
		summary = "Get patient by MRN",
		description = "Retrieves a patient record by its Medical Record Number (MRN). Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Patient Found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PatientDTO.class),
					examples = {
						@ExampleObject(
							name = "Successful Get Patient By MRN Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Patient Found\",\n" +
							"  \"data\": {\n" +
							"    \"id\": 1,\n" +
							"    \"name\": \"John Doe\",\n" +
							"    \"mrn\": \"MRN123456\",\n" +
							"    \"age\": 30,\n" +
							"    \"gender\": \"Male\",\n" +
							"    \"contactNumber\": \"123-456-7890\"\n" +
							"  }\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "404",
				description = "Patient not found",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Patient Not Found Response",
							value = "{\n" +
							"  \"status\": 404,\n" +
							"  \"message\": \"Patient not found\",\n" +
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
	@GetMapping("/mrn/{mrn}")
	public ResponseEntity<ApiResponse<PatientDTO>> getPatientByMrn(
		@RequestHeader("X-User-Role") String roles,
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

	@GetMapping("/userId/{userId}")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getPatientByUserId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long userId
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<PatientDTO> pateintList = patientService.getPatientByUserId(userId);
		if (pateintList != null && !pateintList.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Patients Found", pateintList));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, "Patients not found", null));
		}
	}

	@Operation(
		summary = "Delete patient by ID",
		description = "Deletes a patient record by its unique ID. Accessible by RECEPTIONIST, ADMIN, and USER roles."
	)
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "Patient Deleted Successfully",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Successful Delete Patient Response",
							value = "{\n" +
							"  \"status\": 200,\n" +
							"  \"message\": \"Patient Deleted Successfully\",\n" +
							"  \"data\": null\n" +
							"}"
						)
					}
				)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "204",
				description = "Failed to delete patient",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ApiResponse.class),
					examples = {
						@ExampleObject(
							name = "Failed Delete Patient Response",
							value = "{\n" +
							"  \"status\": 204,\n" +
							"  \"message\": \"Failed to delete patient\",\n" +
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
							"\"data\": null\n" +
							"}"
						)
					}
				)
			)
		}
	)
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> deletePatient(
		@RequestHeader("X-User-Role") String roles,
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
