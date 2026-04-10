package com.cognizant.billingService.respository;

import com.cognizant.billingService.domain.Payment;
import com.cognizant.billingService.domain.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByPatientId(Long patientId);

	Optional<Payment> findByInvoiceId(Long invoiceId);

	List<Payment> findByPaymentStatus(PaymentStatus status);
}
