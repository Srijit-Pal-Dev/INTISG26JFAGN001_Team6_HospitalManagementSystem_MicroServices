package com.cognizant.prescriptionservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "LAB-SERVICE",
        fallback = LabClientFallback.class
)
public interface LabClient {

    @PostMapping("/lab-tests/create")
    void createLabTestRequest(@RequestBody LabTestRequest request);

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class LabTestRequest {
        private Long prescriptionId;
        private Long appointmentId;
        private Long patientId;
        private List<LabTestItem> tests;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class LabTestItem {
        private String testCode;
        private String testName;
        private String notes;
    }
}