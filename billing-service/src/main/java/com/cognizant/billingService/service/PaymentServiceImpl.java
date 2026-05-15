package com.cognizant.billingService.service;

import com.cognizant.billingService.client.NotificationServiceClient;
import com.cognizant.billingService.domain.*;
import com.cognizant.billingService.dto.NotificationDTO;
import com.cognizant.billingService.dto.NotificationType;
import com.cognizant.billingService.dto.PaymentDTO;
import com.cognizant.billingService.exception.ResourceNotFoundException;
import com.cognizant.billingService.mapper.PaymentMapper;
import com.cognizant.billingService.respository.InvoiceRepository;
import com.cognizant.billingService.respository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final InvoiceRepository invoiceRepository;
	private final NotificationServiceClient notificationClient;

	/* Constructor-based dependency injection for repositories and notification client */
	public PaymentServiceImpl(
		PaymentRepository paymentRepository,
		InvoiceRepository invoiceRepository,
		NotificationServiceClient notificationClient
	) {
		this.paymentRepository = paymentRepository;
		this.invoiceRepository = invoiceRepository;
		this.notificationClient = notificationClient;
	}

	/* this method initiates a payment for a given invoice. It checks if the invoice exists
    and is ready for payment, then creates a new payment record with PENDING status, saves
    it to the database, and sends a notification to the patient about the invoice being ready
    for payment. Finally, it returns the created payment as a DTO. */
	@Override
	@Transactional
	public PaymentDTO initiatePayment(Long invoiceId) {
		Invoice invoice = invoiceRepository
			.findById(invoiceId)
			.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

		if (invoice.getInvoiceStatus() != InvoiceStatus.READY) {
			throw new IllegalStateException("Invoice is not ready for payment");
		}
		if (invoice.getTotalAmount() == null || invoice.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("Invoice total amount is invalid for payment");
		}

		Payment payment = new Payment();
		payment.setInvoice(invoice);
		payment.setAppointmentId(invoice.getAppointmentId());
		payment.setPatientId(invoice.getPatientId());
		payment.setAmount(invoice.getTotalAmount());
		payment.setPaymentStatus(PaymentStatus.PENDING);

		Payment saved = paymentRepository.save(payment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(invoice.getPatientId())
			.title("Invoice Ready for Payment")
			.message(
				"Your invoice " +
				invoice.getInvoiceNumber() +
				" is ready for payment. Total amount: " +
				invoice.getTotalAmount()
			)
			.type(NotificationType.BILLING)
			.build();
		createNotification(notification);
		return PaymentMapper.toDTO(saved);
	}

	/* this method updates an existing payment record. It first retrieves the payment by its ID,
    then updates its status to COMPLETED, sets the payment method and amount based on the
    provided DTO, saves the updated payment to the database, and returns the updated payment
    as a DTO. */
	@Override
	@Transactional
	public PaymentDTO updatePayment(PaymentDTO paymentDTO) {
		Payment payment = paymentRepository
			.findById(paymentDTO.getId())
			.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentDTO.getId()));

		payment.setPaymentStatus(PaymentStatus.COMPLETED);
		payment.setPaymentMethod(paymentDTO.getPaymentMethod());
		payment.setAmount(paymentDTO.getAmount());

		Payment updated = paymentRepository.save(payment);
		return PaymentMapper.toDTO(updated);
	}

	/*this method confirms a payment by updating its status to COMPLETED, generating a unique
    transaction ID, setting the payment method, and saving the updated payment to the database.
    It also sends a notification to the patient confirming the payment. Finally, it returns the
    confirmed payment as a DTO. */
	@Override
	@Transactional
	public PaymentDTO confirmPayment(Long userId, Long paymentId, PaymentMethod method) {
		Payment payment = paymentRepository
			.findById(paymentId)
			.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

		payment.setPaymentStatus(PaymentStatus.COMPLETED);
		String transactionId = UUID.randomUUID().toString();
		payment.setTransactionId(transactionId);
		payment.setPaymentMethod(method);

        Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElse(null);
        if (invoice != null) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

		Payment updated = paymentRepository.save(payment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(userId)
			.title("Payment Confirmation")
			.message("Your payment with transaction ID " + transactionId + " has been confirmed.")
			.type(NotificationType.BILLING)
			.build();
		createNotification(notification);
		return PaymentMapper.toDTO(updated);
	}

	/* this method retrieves a payment by its ID. It checks if the payment exists and returns
    its DTO. If the payment is not found, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public PaymentDTO getPaymentById(Long paymentId) {
		Payment payment = paymentRepository
			.findById(paymentId)
			.orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

		return PaymentMapper.toDTO(payment);
	}

	/* this method retrieves all payments from the database, converts them to DTOs, and
    returns the list of payment DTOs. */
	@Override
	@Transactional
	public List<PaymentDTO> getAllPaymenta() {
		List<Payment> payments = paymentRepository.findAll();
		return payments.stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves all payments for a specific patient ID. It checks if any payments exist
    for the patient and returns a list of their DTOs. If no payments are found for the patient, it
    throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public List<PaymentDTO> getPaymentsByPatientId(Long patientId) {
		List<Payment> payments = paymentRepository.findByPatientId(patientId);
		return payments.stream().map(PaymentMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves a payment by the associated invoice ID. It checks if the payment
    exists for the given invoice and returns its DTO. If no payment is found for the invoice,
    it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public PaymentDTO getPaymentByInvoiceId(Long invoiceId) {
		Payment payment = paymentRepository
			.findByInvoiceId(invoiceId)
			.orElseThrow(() -> new ResourceNotFoundException("Payment not found for invoice: " + invoiceId));
		return PaymentMapper.toDTO(payment);
	}

	/* this method cancels a payment by updating its status to CANCELLED. It first retrieves the
    payment by its ID, then updates its status, saves the updated payment to the database, and
    sends a notification to the patient about the cancellation. Finally, it returns the cancelled
    payment as a DTO. */
	@Override
	@Transactional
	public PaymentDTO cancelPayment(Long paymentId) {
		Payment payment = paymentRepository
			.findById(paymentId)
			.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
		payment.setPaymentStatus(PaymentStatus.CANCELLED);
		Payment cancel = paymentRepository.save(payment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(cancel.getPatientId())
			.title("Payment Cancellation")
			.message("Your payment with transaction ID " + cancel.getTransactionId() + " has been cancelled.")
			.type(NotificationType.BILLING)
			.build();
		createNotification(notification);
		return PaymentMapper.toDTO(cancel);
	}

	/* this method retrieves all payments with a specific status. It checks if any payments
    exist with the given status and returns a list of their DTOs. If no payments are found
    with the specified status, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
		return paymentRepository
			.findByPaymentStatus(status)
			.stream()
			.map(PaymentMapper::toDTO)
			.collect(Collectors.toList());
	}

	/* this method creates a notification by sending it to the notification service client.
    It is annotated with @CircuitBreaker to handle potential failures in the notification
    service. If the notification service is unavailable, the fallback method createNotificationFallback
    will be triggered, which logs the error and returns the original notification without sending it. */
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
