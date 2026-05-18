package com.cognizant.prescriptionservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "PHARMACY-SERVICE",
        fallback = PharmacyClientFallback.class
)
public interface PharmacyClient {

    @PostMapping("/dispense")
    void createDispenseRequest(
            @RequestHeader("X-User-Role") String role,
            @RequestBody DispenseRequest request);

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class DispenseRequest {
        private Long prescriptionId;
        private Long patientId;
        private Long appointmentId;
        private List<MedicineItem> medicines;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class MedicineItem {
        private Long medicineId;
//        private String medicineName;
//        private String dosage;
//        private String frequency;
//        private String duration;
        private Integer quantity;
    }
}