package com.cognizant.labService.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LabTestResponse {

    private Long id;
    private Long patientId;
    private Long appointmentId;
    private String testName;
    private String testCode;
    private String status;
    private BigDecimal fee;
    private LocalDateTime createdAt;


}