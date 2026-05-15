package com.cognizant.billingService.respository;

import com.cognizant.billingService.domain.Invoice;
import com.cognizant.billingService.dto.AppointmentDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	List<Invoice> findByAppointmentId(Long appointmentId);
	Optional<Invoice> findFirstByAppointmentId(Long appointmentId);
	boolean existsByAppointmentId(Long appointmentId);
	List<Invoice> findByPatientId(Long patientId);

	Optional<Invoice> findByPaymentId(Long paymentId);
}
