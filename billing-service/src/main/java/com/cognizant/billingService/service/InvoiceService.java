package com.cognizant.billingService.service;

import com.cognizant.billingService.dto.InvoiceDTO;
import com.cognizant.billingService.dto.LabDTO;
import com.cognizant.billingService.dto.PharmacyDTO;

import java.math.BigDecimal;
import java.util.List;

public interface InvoiceService {
    InvoiceDTO initiateInvoice(Long patientId, Long appointmentId);

    InvoiceDTO updateMedicineFee(Long appointmentId, BigDecimal medicineFee, List<PharmacyDTO> medicines);

    InvoiceDTO updateLabFee(Long appointmentId, BigDecimal labFee, List<LabDTO> labTests);

    InvoiceDTO getInvoiceById(Long id);

    List<InvoiceDTO> getAllInvoices();

    void deleteInvoice(Long id);
}
