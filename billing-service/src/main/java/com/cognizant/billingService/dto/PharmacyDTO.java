package com.cognizant.billingService.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PharmacyDTO {
    private Long id;
    private Long prescriptionId;
    private Long patientId;
    private Long appointmentId;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
