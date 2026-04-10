package com.cognizant.billingService.mapper;

import com.cognizant.billingService.domain.Mediclaim;
import com.cognizant.billingService.dto.MediclaimDTO;

public class MediclaimMapper {

	public static MediclaimDTO toDTO(Mediclaim mediclaim) {
		if (mediclaim == null) {
			return null;
		}
		return MediclaimDTO
			.builder()
			.id(mediclaim.getId())
			.patientId(mediclaim.getPatientId())
			.invoiceId(mediclaim.getInvoiceId())
			.paymentId(mediclaim.getPaymentId())
			.policyNumber(mediclaim.getPolicyNumber())
			.insurerName(mediclaim.getInsurerName())
			.coveragePercentage(mediclaim.getCoveragePercentage())
			.refundAmount(mediclaim.getRefundAmount())
			.status(mediclaim.getStatus())
			.appliedAt(mediclaim.getAppliedAt())
			.processedAt(mediclaim.getProcessedAt())
			.build();
	}

	public static Mediclaim toEntity(MediclaimDTO mediclaimDTO) {
		if (mediclaimDTO == null) {
			return null;
		}
		return Mediclaim
			.builder()
			.id(mediclaimDTO.getId())
			.patientId(mediclaimDTO.getPatientId())
			.invoiceId(mediclaimDTO.getInvoiceId())
			.paymentId(mediclaimDTO.getPaymentId())
			.policyNumber(mediclaimDTO.getPolicyNumber())
			.insurerName(mediclaimDTO.getInsurerName())
			.coveragePercentage(mediclaimDTO.getCoveragePercentage())
			.refundAmount(mediclaimDTO.getRefundAmount())
			.status(mediclaimDTO.getStatus())
			.build();
	}
}
