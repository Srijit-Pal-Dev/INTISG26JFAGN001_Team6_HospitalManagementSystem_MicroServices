package com.cognizant.pharmacyService.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service")
public interface BillingClient {

	@PutMapping("/invoice/update/medicine-fee/{appointmentId}")
	Map<String, Object> updateMedicineFee(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable("appointmentId") Long appointmentId,
		@RequestParam("medicineFee") BigDecimal medicineFee,
		@RequestBody List<PharmacyDTO> medicines
	);

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
