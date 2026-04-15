package com.cognizant.pharmacyService.controller;

import com.cognizant.pharmacyService.client.BillingClient.PharmacyDTO;
import com.cognizant.pharmacyService.dto.MedicineRequest;
import com.cognizant.pharmacyService.dto.MedicineResponse;
import com.cognizant.pharmacyService.service.DispenseService;
import com.cognizant.pharmacyService.service.MedicineService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

	private final MedicineService medicineService;
	private final DispenseService dispenseService;

	public MedicineController(MedicineService medicineService, DispenseService dispenseService) {
		this.medicineService = medicineService;
		this.dispenseService = dispenseService;
	}

	@GetMapping
	public List<MedicineResponse> getAllMedicines(@RequestHeader("X-User-Role") String role) {
		if (!role.equalsIgnoreCase("PHARMACIST") && !role.equalsIgnoreCase("DOCTOR")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
		}

		return medicineService.getAllMedicines();
	}

	@PostMapping("/create")
	public MedicineResponse addMedicine(
		@RequestHeader("X-User-Role") String role,
		@RequestBody MedicineRequest request
	) {
		if (!role.equalsIgnoreCase("PHARMACIST")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pharmacist can add medicines");
		}

		return medicineService.addMedicine(request);
	}

	@GetMapping("/search")
	public List<MedicineResponse> searchMedicines(
		@RequestHeader("X-User-Role") String role,
		@RequestParam String name
	) {
		if (!role.equalsIgnoreCase("DOCTOR")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only doctor can search medicines");
		}

		return medicineService.searchMedicines(name);
	}

	@GetMapping("/{id}")
	public MedicineResponse getById(@RequestHeader("X-User-Role") String role, @PathVariable Long id) {
		if (!role.equalsIgnoreCase("PHARMACIST") && !role.equalsIgnoreCase("DOCTOR")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
		}

		return medicineService.getMedicineById(id);
	}

	@PutMapping("/update/{id}")
	public MedicineResponse updateMedicine(
		@RequestHeader("X-User-Role") String role,
		@PathVariable Long id,
		@RequestBody MedicineRequest request
	) {
		if (!role.equalsIgnoreCase("PHARMACIST")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pharmacist can update medicine");
		}

		return medicineService.updateMedicine(id, request);
	}

	@GetMapping("/appointment/{appointmentId}")
	public List<PharmacyDTO> getMedicinesByAppointmentId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable("appointmentId") Long appointmentId
	) {
		if (!roles.contains("DOCTOR") && !roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pharmacist can update medicine");
		}

		return dispenseService.getMedicinesByAppointmentId(appointmentId);
	}
}
