package com.cognizant.billingService.mapper;

import com.cognizant.billingService.domain.Invoice;
import com.cognizant.billingService.dto.InvoiceDTO;

public class InvoiceMapper {

    public static InvoiceDTO toDTO(Invoice invoice){
        if(invoice == null){
            return null;
        }
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .patientId(invoice.getPatientId())
                .appointmentId(invoice.getAppointmentId())
                .consultationFee(invoice.getConsultationFee())
                .medicineFee(invoice.getMedicineFee())
                .labFee(invoice.getLabFee())
                .totalAmount(invoice.getTotalAmount())
                .invoiceStatus(invoice.getInvoiceStatus())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    public static Invoice toEntity(InvoiceDTO invoiceDTO){
        if(invoiceDTO == null){
            return null;
        }
        Invoice invoice = new Invoice();
        invoice.setId(invoiceDTO.getId());
        invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        invoice.setPatientId(invoiceDTO.getPatientId());
        invoice.setAppointmentId(invoiceDTO.getAppointmentId());
        invoice.setConsultationFee(invoiceDTO.getConsultationFee());
        invoice.setMedicineFee(invoiceDTO.getMedicineFee());
        invoice.setLabFee(invoiceDTO.getLabFee());
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setInvoiceStatus(invoiceDTO.getInvoiceStatus());

        return invoice;
    }
}
