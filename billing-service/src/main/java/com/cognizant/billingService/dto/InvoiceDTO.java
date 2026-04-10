package com.cognizant.billingService.dto;

import com.cognizant.billingService.domain.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private BigDecimal consultationFee;
    private BigDecimal medicineFee;
    private BigDecimal labFee;
    private BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentDTO payment;

    private PatientDTO patient;
    private DoctorDTO doctor;
    private List<PharmacyDTO> medicines;
    private List<LabDTO> labTests;
    private AppointmentDTO appointment;

}
