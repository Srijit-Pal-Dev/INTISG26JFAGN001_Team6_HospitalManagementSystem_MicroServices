package com.cognizant.prescriptionservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "LAB-SERVICE",
        fallback = LabClientFallback.class
)
public interface LabClient {

    @PostMapping("/lab-tests/create")
    List<LabTestResponse> createTest(
            @RequestHeader("X-User-Role") String roles,
            @RequestBody LabTestRequest request);

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class LabTestRequest {
//        private Long prescriptionId;
        private Long appointmentId;
        private List<LabTestResponse> tests;
        private Long patientId;
    }

//    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
//    class LabTestItem {
//        private String testCode;
//        private String testName;
//        private String notes;
//    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    class LabTestResponse {
        private Long id;
        private Long patientId;
        private Long appointmentId;
        private String testName;
        private String testCode;
        private String status;
        private BigDecimal fee;
        private LocalDateTime createdAt;
    }
}