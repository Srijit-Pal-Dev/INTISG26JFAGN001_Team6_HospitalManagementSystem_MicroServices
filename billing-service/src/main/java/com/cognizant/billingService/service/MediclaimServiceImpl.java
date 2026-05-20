package com.cognizant.billingService.service;

import com.cognizant.billingService.client.NotificationServiceClient;
import com.cognizant.billingService.domain.Invoice;
import com.cognizant.billingService.domain.Mediclaim;
import com.cognizant.billingService.domain.MediclaimStatus;
import com.cognizant.billingService.domain.Payment;
import com.cognizant.billingService.dto.MediclaimDTO;
import com.cognizant.billingService.dto.NotificationDTO;
import com.cognizant.billingService.dto.NotificationType;
import com.cognizant.billingService.exception.ResourceNotFoundException;
import com.cognizant.billingService.mapper.MediclaimMapper;
import com.cognizant.billingService.respository.MediclaimRepository;
import com.cognizant.billingService.respository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediclaimServiceImpl implements MediclaimService {

	private final MediclaimRepository mediclaimRepository;
	private final PaymentRepository paymentRepository;
	private final NotificationServiceClient notificationClient;

	/* Constructor-based dependency injection for repositories and notification client */
	public MediclaimServiceImpl(
		MediclaimRepository mediclaimRepository,
		PaymentRepository paymentRepository,
		NotificationServiceClient notificationClient
	) {
		this.mediclaimRepository = mediclaimRepository;
		this.paymentRepository = paymentRepository;
		this.notificationClient = notificationClient;
	}

	/* this method creates a mediclaim for a given payment. It checks if the payment
    exists and is in PAID status, then calculates the refund amount based on the invoice
    total and coverage percentage, saves the mediclaim, and sends a notification to the
    patient. */
	@Override
	@Transactional
	public MediclaimDTO createMediclaim(MediclaimDTO mediclaimDTO) {
		Payment payment = paymentRepository
			.findByInvoiceId(mediclaimDTO.getInvoiceId())
			.orElseThrow(() ->
				new ResourceNotFoundException("No payment found for invoice: " + mediclaimDTO.getInvoiceId())
			);

		if (payment.getTransactionId() == null) {
			throw new IllegalStateException("Mediclaim can only be applied on COMPLETED payments");
		}

		Invoice invoice = payment.getInvoice();

		BigDecimal refundAmount = invoice
			.getTotalAmount()
			.multiply(mediclaimDTO.getCoveragePercentage())
			.divide(BigDecimal.valueOf(100));

		Mediclaim mediclaim = new Mediclaim();
		mediclaim.setInvoiceId(invoice.getId());
		mediclaim.setPaymentId(payment.getId());
		mediclaim.setPatientId(invoice.getPatientId());
		mediclaim.setPolicyNumber(mediclaimDTO.getPolicyNumber());
		mediclaim.setInsurerName(mediclaimDTO.getInsurerName());
		mediclaim.setCoveragePercentage(mediclaimDTO.getCoveragePercentage());
		mediclaim.setRefundAmount(refundAmount);
		mediclaim.setStatus(MediclaimStatus.PENDING);
		mediclaim.setAppliedAt(LocalDateTime.now());

		Mediclaim saved = mediclaimRepository.save(mediclaim);

		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(invoice.getPatientId())
			.title("Mediclaim Applied")
			.message("Your mediclaim for invoice " + invoice.getId() + " has been applied and is pending review.")
			.type(NotificationType.MEDICLAIM)
			.build();
		createNotification(notification);

		return MediclaimMapper.toDTO(saved);
	}

	/* this method updates the status of an existing mediclaim. It checks if the mediclaim
    exists, updates its status, and sends a notification to the patient based on whether the
    claim was approved or rejected. Finally, it saves the updated mediclaim and returns the
    updated DTO. */
	@Override
	@Transactional
	public MediclaimDTO updateMediclaimStatus(Long userId, Long id, MediclaimStatus status) {
		Mediclaim mediclaim = mediclaimRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Mediclaim with id " + id + " not found"));

		mediclaim.setStatus(status);
		if (status == MediclaimStatus.APPROVED) {
			NotificationDTO notification = NotificationDTO
				.builder()
				.userId(userId)
				.title("Mediclaim Approved")
				.message(
					"Your mediclaim for invoice " +
					mediclaim.getInvoiceId() +
					" has been approved. Refund amount: " +
					mediclaim.getRefundAmount()
				)
				.type(NotificationType.MEDICLAIM)
				.build();
			createNotification(notification);
		} else if (status == MediclaimStatus.REJECTED) {
			NotificationDTO notification = NotificationDTO
				.builder()
				.userId(userId)
				.title("Mediclaim Rejected")
				.message(
					"Your mediclaim for invoice " +
					mediclaim.getInvoiceId() +
					" has been rejected. Please contact support for more details."
				)
				.type(NotificationType.MEDICLAIM)
				.build();
			createNotification(notification);
		}

		Mediclaim updated = mediclaimRepository.save(mediclaim);
		return MediclaimMapper.toDTO(updated);
	}

	/* this method retrieves a mediclaim by its ID. It checks if the mediclaim exists and
    returns its DTO. If the mediclaim is not found, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public MediclaimDTO getMediclaimById(Long id) {
		Mediclaim mediclaim = mediclaimRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Mediclaim with id " + id + " not found"));
		return MediclaimMapper.toDTO(mediclaim);
	}

	/* this method retrieves all mediclaims for a given patient ID. It checks if any mediclaims
    exist for the patient and returns a list of their DTOs. If no mediclaims are found,
    it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public List<MediclaimDTO> getAllMediclaimsByPatientId(Long patientId) {
		List<Mediclaim> mediclaims = mediclaimRepository.findByPatientId(patientId);
		if (mediclaims.isEmpty()) {
			return Collections.emptyList();
		}
		return mediclaims.stream().map(MediclaimMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves all mediclaims in the system. It checks if any mediclaims exist and
    returns a list of their DTOs. If no mediclaims are found, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public List<MediclaimDTO> getAllMediclaims() {
		List<Mediclaim> mediclaims = mediclaimRepository.findAll();
		if (mediclaims.isEmpty()) {
			throw new ResourceNotFoundException("No mediclaims found");
		}
		return mediclaims.stream().map(MediclaimMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves all mediclaims with a specific status. It checks if any
    mediclaims exist with the given status and returns a list of their DTOs. If no mediclaims
    are found with the specified status, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public List<MediclaimDTO> getMediclaimsByStatus(MediclaimStatus status) {
		List<Mediclaim> mediclaims = mediclaimRepository.findByStatus(status);
		if (mediclaims.isEmpty()) {
			throw new ResourceNotFoundException("No mediclaims found with status " + status);
		}
		return mediclaims.stream().map(MediclaimMapper::toDTO).collect(Collectors.toList());
	}

	/* this method is responsible for sending a notification to the patient. It uses the
    NotificationServiceClient to send the notification and is annotated with @CircuitBreaker
    to handle potential failures in the notification service. If the notification service is
    unavailable, the fallback method createNotificationFallback will be triggered, which
    logs the error and returns the original notification without sending it. */
	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "createNotificationFallback")
	private NotificationDTO createNotification(NotificationDTO notification) {
		notificationClient.send(notification);
		return notification;
	}

	private NotificationDTO createNotificationFallback(NotificationDTO notification, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for notification: " + t.getMessage());
		return notification;
	}
}
