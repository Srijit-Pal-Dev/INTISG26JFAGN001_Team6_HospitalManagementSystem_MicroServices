package com.cognizant.labService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabResultResponse {

    private Long id;
    private Long labTestId;
    private String resultValue;
    private String unit;
    private String referenceRange;
    private Boolean isAbnormal;
    private String notes;
    private String recordedBy;
    private BigDecimal fee;
    private LocalDateTime recordedAt;


}