package com.cognizant.pharmacyService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "billing-service", fallback = BillingClient.BillingClientFallback.class)
public interface BillingClient {

	@PutMapping("/invoice/update/medicine-fee/{appointmentId}")
	ResponseEntity<ApiResponse<InvoiceDTO>> updateMedicineFee(@RequestHeader("X-User-Role") String roles,
			@PathVariable("appointmentId") Long appointmentId, @RequestParam("medicineFee") BigDecimal medicineFee,
			@RequestBody List<PharmacyDTO> medicines);

	@Component
	class BillingClientFallback implements BillingClient {

		@Override
		public ResponseEntity<ApiResponse<InvoiceDTO>> updateMedicineFee(String roles, Long invoiceId, BigDecimal medicineFee,
				List<PharmacyDTO> medicines) {

			System.out.println("Billing service unavailable. Skipping medicine fee update.");

			return ResponseEntity.status(503).body(new ApiResponse<>(503, "Billing service unavailable", null));
		}
	}

	class ApiResponse<T> {
		private int statusCode;
		private String message;
		private T data;

		public ApiResponse() {
		}

		public ApiResponse(int statusCode, String message, T data) {
			this.statusCode = statusCode;
			this.message = message;
			this.data = data;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}

	class InvoiceDTO {
		private Long id;
		private Long appointmentId;
		private BigDecimal medicineFee;
		private BigDecimal totalAmount;
		private String invoiceStatus;
		private LocalDateTime updatedAt;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getAppointmentId() {
			return appointmentId;
		}

		public void setAppointmentId(Long appointmentId) {
			this.appointmentId = appointmentId;
		}

		public BigDecimal getMedicineFee() {
			return medicineFee;
		}

		public void setMedicineFee(BigDecimal medicineFee) {
			this.medicineFee = medicineFee;
		}

		public BigDecimal getTotalAmount() {
			return totalAmount;
		}

		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}

		public String getInvoiceStatus() {
			return invoiceStatus;
		}

		public void setInvoiceStatus(String invoiceStatus) {
			this.invoiceStatus = invoiceStatus;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
	}

	class PharmacyDTO {
		private Long medicineId;
		private String medicineName;
		private Integer quantity;
		private BigDecimal unitPrice;
		private BigDecimal totalPrice;

		public Long getMedicineId() {
			return medicineId;
		}

		public void setMedicineId(Long medicineId) {
			this.medicineId = medicineId;
		}

		public String getMedicineName() {
			return medicineName;
		}

		public void setMedicineName(String medicineName) {
			this.medicineName = medicineName;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}

		public BigDecimal getTotalPrice() {
			return totalPrice;
		}

		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
	}
}