package com.cognizant.pharmacyService.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service", fallback = BillingClient.BillingClientFallback.class)
public interface BillingClient {

    @PutMapping("/invoice/update/medicine-fee/{appointmentId}")
    Map<String, Object> updateMedicineFee(
            @RequestHeader("X-User-Role") String roles,
            @PathVariable("appointmentId") Long appointmentId,
            @RequestParam("medicineFee") BigDecimal medicineFee,
            @RequestBody List<PharmacyDTO> medicines
    );

    @Component
    class BillingClientFallback implements BillingClient {

        @Override
        public Map<String, Object> updateMedicineFee(
                String roles,
                Long appointmentId,
                BigDecimal medicineFee,
                List<PharmacyDTO> medicines) {

            System.out.println("Billing service unavailable. Skipping invoice update for appointmentId="
                    + appointmentId);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("status", "FAILED");
            response.put("message", "Billing service is currently unavailable. Medicine fee update skipped.");
            response.put("appointmentId", appointmentId);
            response.put("medicineFee", medicineFee);
            response.put("timestamp", java.time.Instant.now().toString());

            return response;
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
