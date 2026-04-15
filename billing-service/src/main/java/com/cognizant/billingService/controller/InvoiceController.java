package com.cognizant.billingService.controller;

import com.cognizant.billingService.dto.InvoiceDTO;
import com.cognizant.billingService.dto.LabDTO;
import com.cognizant.billingService.dto.PharmacyDTO;
import com.cognizant.billingService.exception.InvalidRoleException;
import com.cognizant.billingService.service.InvoiceService;
import com.cognizant.billingService.util.ApiResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@PostMapping("/generate/{patientId}/{appointmentId}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> initiateInvoice(
		@PathVariable Long patientId,
		@PathVariable Long appointmentId
	) {
		InvoiceDTO invoice = invoiceService.initiateInvoice(patientId, appointmentId);
		if (invoice != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Invoice Generated Successfully", invoice));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to generate invoice", null));
		}
	}

	@PutMapping("/update/medicine-fee/{appointmentId}/{medicineFee}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> updateMedicineFee(
		@RequestHeader("X-User-Role") String roles,
		@RequestHeader("X-User-Id") Long userId,
		@PathVariable("appointmentId") Long appointmentId,
		@PathVariable("medicineFee") BigDecimal medicineFee,
		@RequestBody List<PharmacyDTO> medicines
	) {
		if (!roles.contains("ADMIN") && !roles.contains("PHARMACIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		InvoiceDTO updatedInvoice = invoiceService.updateMedicineFee(userId, appointmentId, medicineFee, medicines);
		if (updatedInvoice != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Invoice Updated Successfully", updatedInvoice));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update invoice", null));
		}
	}

	@PutMapping("/update/lab-fee/{appointmentId}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> updateLabFee(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long appointmentId,
		@RequestParam BigDecimal labFee,
		@RequestBody List<LabDTO> labTests
	) {
		if (!roles.contains("ADMIN") && !roles.contains("LAB_TECHNICIAN")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		InvoiceDTO updatedInvoice = invoiceService.updateLabFee(appointmentId, labFee, labTests);
		if (updatedInvoice != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Invoice Updated Successfully", updatedInvoice));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update invoice", null));
		}
	}

	@GetMapping("/id/{invoiceId}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long invoiceId
	) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		InvoiceDTO invoice = invoiceService.getInvoiceById(invoiceId);
		if (invoice != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Invoice Retrieved Successfully", invoice));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Invoice not found with id: " + invoiceId, null));
		}
	}

	@GetMapping("/all-invoices")
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices(@RequestHeader("X-User-Role") String roles) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
		if (invoices != null && !invoices.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Invoices Retrieved Successfully", invoices));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "No invoices found", null));
		}
	}

	@DeleteMapping("/delete/{invoiceId}")
	public ResponseEntity<ApiResponse<Void>> deleteInvoice(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long invoiceId
	) {
		if (!roles.contains("ADMIN")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		try {
			InvoiceDTO invoiceDTO = invoiceService.getInvoiceById(invoiceId);
			if (invoiceDTO != null) {
				invoiceService.deleteInvoice(invoiceId);
				return ResponseEntity.ok(new ApiResponse<>(200, "Invoice Deleted Successfully", null));
			} else {
				return ResponseEntity
					.status(404)
					.body(new ApiResponse<>(404, "Invoice not found with id: " + invoiceId, null));
			}
		} catch (Exception e) {
			return ResponseEntity
				.status(500)
				.body(new ApiResponse<>(500, "An error occurred while deleting the invoice", null));
		}
	}
}
