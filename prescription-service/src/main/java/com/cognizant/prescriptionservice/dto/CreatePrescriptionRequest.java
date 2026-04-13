package com.cognizant.prescriptionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePrescriptionRequest {

    @NotNull
    private Long appointmentId;

    @NotNull
    private Long patientId;

    private String diagnosis;
    private String doctorNotes;

    private Boolean labRequired;

    private List<PrescriptionMedicineRequest> medicines;
    private List<PrescriptionLabTestRequest> labTests;
}