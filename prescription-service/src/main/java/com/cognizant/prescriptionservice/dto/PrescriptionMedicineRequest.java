package com.cognizant.prescriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionMedicineRequest {

    private Long medicineId;

    @NotBlank
    private String medicineName;

    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}