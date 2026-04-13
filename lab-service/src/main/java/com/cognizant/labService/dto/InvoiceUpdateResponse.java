package com.cognizant.labService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceUpdateResponse {
    private Long id;
    private String invoiceNumber;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private BigDecimal labFee;
    private BigDecimal totalAmount;

}
