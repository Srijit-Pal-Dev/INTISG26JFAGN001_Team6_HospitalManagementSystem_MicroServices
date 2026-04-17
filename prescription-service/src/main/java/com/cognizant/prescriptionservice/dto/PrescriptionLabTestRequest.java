package com.cognizant.prescriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionLabTestRequest {

//    private String testCode;

    @NotBlank
    private String testName;

    private String notes;
}