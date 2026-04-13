package com.cognizant.prescriptionservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfileRequest {

    @NotBlank
    private String fullName;

    private String specialty;

    private String qualification;

    @Min(0)
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal consultationFee;
}