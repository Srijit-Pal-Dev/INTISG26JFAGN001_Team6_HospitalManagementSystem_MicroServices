package com.cognizant.prescriptionservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;
    private Long userId;

    private String fullName;
    private String specialty;
    private String qualification;
    private Integer experienceYears;
    private BigDecimal consultationFee;
}