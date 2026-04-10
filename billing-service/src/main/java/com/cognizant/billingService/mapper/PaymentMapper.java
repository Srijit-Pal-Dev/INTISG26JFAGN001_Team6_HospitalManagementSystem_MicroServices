package com.cognizant.billingService.mapper;

import com.cognizant.billingService.domain.Payment;
import com.cognizant.billingService.dto.PaymentDTO;

public class PaymentMapper {

	public static PaymentDTO toDTO(Payment payment) {
		if (payment == null) return null;
		return PaymentDTO
			.builder()
			.id(payment.getId())
			.patientId(payment.getPatientId())
			.appointmentId(payment.getAppointmentId())
			.transactionId(payment.getTransactionId())
			.paymentMethod(payment.getPaymentMethod())
			.paymentStatus(payment.getPaymentStatus())
			.amount(payment.getAmount())
			.createdAt(payment.getCreatedAt())
			.updatedAt(payment.getUpdatedAt())
			.build();
	}

	public static Payment toEntity(PaymentDTO paymentDTO) {
		if (paymentDTO == null) return null;
		Payment payment = new Payment();
		payment.setId(paymentDTO.getId());
		payment.setPatientId(paymentDTO.getPatientId());
		payment.setAppointmentId(paymentDTO.getAppointmentId());
		payment.setTransactionId(paymentDTO.getTransactionId());
		payment.setPaymentMethod(paymentDTO.getPaymentMethod());
		payment.setPaymentStatus(paymentDTO.getPaymentStatus());
		payment.setAmount(paymentDTO.getAmount());
		return payment;
	}
}
