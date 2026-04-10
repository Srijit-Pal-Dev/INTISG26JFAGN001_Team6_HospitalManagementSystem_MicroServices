package com.cognizant.billingService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabDTO {

    private Long id;
    private Long patientId;
    private Long appointmentId;
    private String testName;
    private String testCode;
    private String status;
    private Double fee;
    private LocalDateTime createdAt;
}
