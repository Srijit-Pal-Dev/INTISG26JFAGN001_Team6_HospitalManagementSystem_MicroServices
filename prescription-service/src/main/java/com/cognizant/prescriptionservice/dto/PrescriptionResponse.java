package com.cognizant.prescriptionservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionResponse {

    private Long id;
    private Long appointmentId;
    private Long patientId;

    private Long doctorId;
    private String doctorName;

    private String diagnosis;
    private String doctorNotes;
    private Boolean labRequired;

    private List<PrescriptionMedicineRequest> medicines;
    private List<PrescriptionLabTestRequest> labTests;

    private LocalDateTime createdAt;
}
