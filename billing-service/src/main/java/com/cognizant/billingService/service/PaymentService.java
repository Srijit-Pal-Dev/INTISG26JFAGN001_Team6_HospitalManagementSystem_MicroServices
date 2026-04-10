package com.cognizant.billingService.service;

import com.cognizant.billingService.domain.PaymentMethod;
import com.cognizant.billingService.domain.PaymentStatus;
import com.cognizant.billingService.dto.PaymentDTO;
import java.util.List;

public interface PaymentService {
	PaymentDTO initiatePayment(Long invoiceId);

	PaymentDTO updatePayment(PaymentDTO paymentDTO);

	PaymentDTO confirmPayment(Long paymentId, PaymentMethod method);

	PaymentDTO getPaymentById(Long paymentId);

	List<PaymentDTO> getAllPaymenta();

	List<PaymentDTO> getPaymentsByPatientId(Long patientId);

	PaymentDTO getPaymentByInvoiceId(Long invoiceId);

	PaymentDTO cancelPayment(Long paymentId);

	List<PaymentDTO> getPaymentsByStatus(PaymentStatus status);
}
