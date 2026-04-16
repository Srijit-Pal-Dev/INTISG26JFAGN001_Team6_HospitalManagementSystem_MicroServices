package com.cognizant.billingService.controller;

import com.cognizant.billingService.domain.PaymentMethod;
import com.cognizant.billingService.domain.PaymentStatus;
import com.cognizant.billingService.dto.PaymentDTO;
import com.cognizant.billingService.exception.InvalidRoleException;
import com.cognizant.billingService.service.PaymentServiceImpl;
import com.cognizant.billingService.util.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	private final PaymentServiceImpl paymentService;

	public PaymentController(PaymentServiceImpl paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/initiate/{invoiceId}")
	public ResponseEntity<ApiResponse<PaymentDTO>> initiatePayment(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long invoiceId
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("LAB_TECHNICIAN") &&
			!roles.contains("PHARMACIST")
		) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO payment = paymentService.initiatePayment(invoiceId);
		if (payment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Initiated Successfully", payment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to initiate payment", null));
		}
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<PaymentDTO>> updatePayment(
		@RequestHeader("X-User-Role") String roles,
		@RequestBody PaymentDTO paymentDTO
	) {
		if (
			!roles.contains("RECEPTIONIST") &&
			!roles.contains("ADMIN") &&
			!roles.contains("LAB_TECHNICIAN") &&
			!roles.contains("PHARMACIST")
		) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO updatedPayment = paymentService.updatePayment(paymentDTO);
		if (updatedPayment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Updated Successfully", updatedPayment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to update payment", null));
		}
	}

	@PutMapping("/complete/{paymentId}")
	public ResponseEntity<ApiResponse<PaymentDTO>> completePayment(
		@RequestHeader("X-User-Role") String roles,
		@RequestHeader(value = "X-User-Id", required = false) Long userId,
		@PathVariable Long paymentId,
		@RequestParam PaymentMethod paymentMethod
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO completePayment = paymentService.confirmPayment(userId, paymentId, paymentMethod);
		if (completePayment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Completed Successfully", completePayment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to complete payment", null));
		}
	}

	@PutMapping("/cancel/{paymentId}")
	public ResponseEntity<ApiResponse<PaymentDTO>> cancelPayment(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long paymentId
	) {
		if (!roles.contains("RECEPTIONIST") && !roles.contains("ADMIN") && !roles.contains("USER")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO cancelPayment = paymentService.cancelPayment(paymentId);
		if (cancelPayment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Cancelled Successfully", cancelPayment));
		} else {
			return ResponseEntity.status(400).body(new ApiResponse<>(400, "Failed to cancel payment", null));
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long id
	) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO paymentDTO = paymentService.getPaymentById(id);
		if (paymentDTO != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Retrieved Successfully", paymentDTO));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Payment not found with id: " + id, null));
		}
	}

	@GetMapping("/all-payments")
	public ResponseEntity<ApiResponse<List<PaymentDTO>>> getAllPayments(@RequestHeader("X-User-Role") String roles) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<PaymentDTO> payments = paymentService.getAllPaymenta();
		if (payments != null && !payments.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payments Found", payments));
		} else {
			return ResponseEntity.status(404).body(new ApiResponse<>(404, "Payments not found", null));
		}
	}

	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByPatientId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long patientId
	) {
		if (!roles.contains("ADMIN") && !roles.contains("USER") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<PaymentDTO> payments = paymentService.getPaymentsByPatientId(patientId);
		if (payments != null && !payments.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payments Found", payments));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Payments not found for patient id: " + patientId, null));
		}
	}

	@GetMapping("/invoice/{invoiceId}")
	public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentsByInvoiceId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable Long invoiceId
	) {
		if (!roles.contains("ADMIN") && !roles.contains("USER") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		PaymentDTO payment = paymentService.getPaymentByInvoiceId(invoiceId);
		if (payment != null) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payment Found", payment));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Payment not found for invoice id: " + invoiceId, null));
		}
	}

	@GetMapping("/status")
	public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByStatus(
		@RequestHeader("X-User-Role") String roles,
		@RequestParam PaymentStatus status
	) {
		if (!roles.contains("ADMIN") && !roles.contains("RECEPTIONIST")) {
			throw new InvalidRoleException("Forbidden: You don't have permission to access this resource");
		}
		List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
		if (payments != null && !payments.isEmpty()) {
			return ResponseEntity.ok(new ApiResponse<>(200, "Payments Found", payments));
		} else {
			return ResponseEntity
				.status(404)
				.body(new ApiResponse<>(404, "Payments not found with status: " + status, null));
		}
	}
}
