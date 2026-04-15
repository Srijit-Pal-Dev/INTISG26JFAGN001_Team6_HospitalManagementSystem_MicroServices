package com.cognizant.billingService.service;

import com.cognizant.billingService.client.*;
import com.cognizant.billingService.domain.Invoice;
import com.cognizant.billingService.domain.InvoiceStatus;
import com.cognizant.billingService.domain.Payment;
import com.cognizant.billingService.domain.PaymentStatus;
import com.cognizant.billingService.dto.*;
import com.cognizant.billingService.mapper.InvoiceMapper;
import com.cognizant.billingService.respository.InvoiceRepository;
import com.cognizant.billingService.respository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final PaymentRepository paymentRepository;
	private final DoctorServiceClient doctorClient;
	private final PatientServiceClient patientClient;
	private final PharmacyServiceClient pharmacyClient;
	private final LabServiceClient labClient;
	private final NotificationServiceClient notificationClient;

	/* Constructor-based dependency injection for all required repositories and clients. */
	public InvoiceServiceImpl(
		InvoiceRepository invoiceRepository,
		PaymentRepository paymentRepository,
		DoctorServiceClient doctorClient,
		PatientServiceClient patientClient,
		PharmacyServiceClient pharmacyClient,
		LabServiceClient labClient,
		NotificationServiceClient notificationClient
	) {
		this.invoiceRepository = invoiceRepository;
		this.paymentRepository = paymentRepository;
		this.doctorClient = doctorClient;
		this.patientClient = patientClient;
		this.pharmacyClient = pharmacyClient;
		this.labClient = labClient;
		this.notificationClient = notificationClient;
	}

	/* this method is called by doctor service when an appointment is completed.
    It creates a new invoice with consultation fee and sets status to PENDING if there
    are medicines or labs, otherwise READY. If READY, it also auto-creates a payment
    record and sends notification to patient. */
	@Override
	public InvoiceDTO initiateInvoice(Long patientId, Long appointmentId) {
		System.out.println("Initiating invoice for patientId: " + patientId + ", appointmentId: " + appointmentId);
		PatientDTO patient = getPatientInfo(patientId);
		System.out.println("Patient : " + patient);
		AppointmentDTO appointment = getAppointmentInfo(appointmentId);
		System.out.println("Appointment : " + appointment);
		DoctorDTO doctor = getDoctorInfo(appointment.getDoctorId());
		System.out.println("Doctor : " + appointment.getDoctorId());
		System.out.println(getNotification(1L));
		Long count = invoiceRepository.count() + 1;
		String generatedInvoice = "INV" + String.format("%05d", count);

		Invoice invoice = new Invoice();
		invoice.setPatientId(patient.getId());
		invoice.setDoctorId(doctor.getId());
		invoice.setAppointmentId(appointment.getId());
		invoice.setConsultationFee(doctor.getConsultationFee());
		invoice.setInvoiceNumber(generatedInvoice);
		List<PharmacyDTO> medicines = getMedicinesByAppointmentId(appointmentId);
		//List<LabDTO> labTests = getLabTestsByAppointmentId(appointmentId);
		if ((medicines == null || medicines.isEmpty())) {
			// No medicines or labs -> invoice is READY immediately
			invoice.calculateTotalAmount();
			invoice.setInvoiceStatus(InvoiceStatus.READY);
			Invoice savedInvoice = invoiceRepository.save(invoice);
			// Auto create payment record
			createPaymentForInvoice(savedInvoice, patient.getId());
			NotificationDTO notification = NotificationDTO
				.builder()
				.userId(patient.getId())
				.title("Invoice Ready for Payment")
				.message(
					"Your invoice " +
					savedInvoice.getInvoiceNumber() +
					" is ready for payment. Total amount: " +
					savedInvoice.getTotalAmount()
				)
				.type(NotificationType.BILLING)
				.build();
			createNotification(notification);
			InvoiceDTO dto = InvoiceMapper.toDTO(savedInvoice);
			dto.setPatient(patient);
			dto.setDoctor(doctor);
			dto.setAppointment(appointment);
			return dto;
		} else {
			// Medicines or labs exist -> invoice stays PENDING until fees are updated
			invoice.setInvoiceStatus(InvoiceStatus.PENDING);
			Invoice savedInvoice = invoiceRepository.save(invoice);
			InvoiceDTO dto = InvoiceMapper.toDTO(savedInvoice);
			dto.setPatient(patient);
			dto.setDoctor(doctor);
			dto.setAppointment(appointment);
			return dto;
		}
	}

	/* this method is called by pharmacy service when medicines are dispensed and
    fees are calculated. It updates the medicine fee in the invoice and checks if
    lab fee is already updated. If both fees are present, it calculates total, sets
    status to READY, creates payment record and sends notification to patient. If lab
    fee is not yet updated, it keeps status as PENDING. */
	@Override
	public InvoiceDTO updateMedicineFee(
		Long userId,
		Long appointmentId,
		BigDecimal medicineFee,
		List<PharmacyDTO> medicines
	) {
		Invoice invoice = invoiceRepository
			.findById(appointmentId)
			.orElseThrow(() -> new RuntimeException("Appointment with id " + appointmentId + " not found"));

		invoice.setMedicineFee(medicineFee);
		//		List<LabDTO> labTests = getLabTestsByAppointmentId(invoice.getAppointmentId());
		//		if (labTests == null || labTests.isEmpty()) {
		if (true) {
			invoice.setTotalAmount(invoice.calculateTotalAmount());
			invoice.setInvoiceStatus(InvoiceStatus.READY);
			Invoice updatedInvoice = invoiceRepository.save(invoice);
			createPaymentForInvoice(updatedInvoice, invoice.getPatientId());
			NotificationDTO notification = NotificationDTO
				.builder()
				.userId(userId)
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
			InvoiceDTO dto = InvoiceMapper.toDTO(updatedInvoice);
			dto.setMedicines(medicines);
			return dto;
		} else {
			invoice.setInvoiceStatus(InvoiceStatus.PENDING);
			return InvoiceMapper.toDTO(invoiceRepository.save(invoice));
		}
	}

	/* this method is called by lab service when lab tests are completed and fees are
    calculated. It updates the lab fee in the invoice and checks if medicine fee is already
    updated. If both fees are present, it calculates total, sets status to READY, creates
    payment record and sends notification to patient. If medicine fee is not yet updated,
    it keeps status as PENDING. */
	@Override
	public InvoiceDTO updateLabFee(Long appointmentId, BigDecimal labFee, List<LabDTO> labTests) {
		Invoice invoice = invoiceRepository
			.findById(appointmentId)
			.orElseThrow(() -> new RuntimeException("Appointment with id " + appointmentId + " not found"));

		invoice.setLabFee(labFee);
		List<PharmacyDTO> medicines = getMedicinesByAppointmentId(invoice.getAppointmentId());
		if (medicines == null || medicines.isEmpty()) {
			invoice.calculateTotalAmount();
			invoice.setInvoiceStatus(InvoiceStatus.READY);
			Invoice updatedInvoice = invoiceRepository.save(invoice);
			createPaymentForInvoice(updatedInvoice, invoice.getPatientId());
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
			InvoiceDTO dto = InvoiceMapper.toDTO(updatedInvoice);
			dto.setLabTests(labTests);
			return dto;
		} else {
			invoice.setInvoiceStatus(InvoiceStatus.PENDING);
			return InvoiceMapper.toDTO(invoiceRepository.save(invoice));
		}
	}

	/* this method is called for retrieving invoice details by id. It fetches the invoice
    from database and then calls external services to get patient, doctor, appointment,
    medicines and lab tests details. It then constructs a comprehensive InvoiceDTO with all
    the details and returns it. If invoice is not found, it throws an exception. */
	@Override
	public InvoiceDTO getInvoiceById(Long id) {
		Invoice invoice = invoiceRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Invoice with id " + id + " not found"));
		PatientDTO patient = getPatientInfo(invoice.getPatientId());
		DoctorDTO doctor = getDoctorInfo(invoice.getDoctorId());
		AppointmentDTO appointment = getAppointmentInfo(invoice.getAppointmentId());
		List<PharmacyDTO> medicines = getMedicinesByAppointmentId(invoice.getAppointmentId());
		List<LabDTO> labTests = getLabTestsByAppointmentId(invoice.getAppointmentId());

		InvoiceDTO dto = InvoiceMapper.toDTO(invoice);
		dto.setPatient(patient);
		dto.setDoctor(doctor);
		dto.setAppointment(appointment);
		dto.setMedicines(medicines);
		dto.setLabTests(labTests);
		return dto;
	}

	/* this method is called for retrieving all invoices. It fetches all invoices from
    database and then for each invoice, it calls external services to get patient, doctor,
    appointment, medicines and lab tests details. It then constructs a comprehensive InvoiceDTO
    with all the details and returns the list of InvoiceDTOs. If no invoices are found, it
    returns an empty list. */
	@Override
	public List<InvoiceDTO> getAllInvoices() {
		List<Invoice> invoices = invoiceRepository.findAll();
		return invoices
			.stream()
			.map(invoice -> {
				PatientDTO patient = getPatientInfo(invoice.getPatientId());
				DoctorDTO doctor = getDoctorInfo(invoice.getDoctorId());
				AppointmentDTO appointment = getAppointmentInfo(invoice.getAppointmentId());
				List<PharmacyDTO> medicines = getMedicinesByAppointmentId(invoice.getAppointmentId());
				List<LabDTO> labTests = getLabTestsByAppointmentId(invoice.getAppointmentId());

				InvoiceDTO dto = InvoiceMapper.toDTO(invoice);
				dto.setPatient(patient);
				dto.setDoctor(doctor);
				dto.setAppointment(appointment);
				dto.setMedicines(medicines);
				dto.setLabTests(labTests);
				return dto;
			})
			.collect(Collectors.toList());
	}

	/* this method is called for deleting an invoice by id. It first checks if the invoice
    exists in the database. If it does, it deletes the invoice. If it does not exist, it
    throws an exception. */
	@Override
	public void deleteInvoice(Long id) {
		Invoice invoice = invoiceRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Invoice with id " + id + " not found"));

		invoiceRepository.deleteById(id);
	}

	/* this is a helper method to create a payment record for an invoice. It takes the invoice
    and patient id as input, creates a new payment record with the invoice details and saves it
    to the database. The payment status is set to PENDING initially. */
	private void createPaymentForInvoice(Invoice invoice, Long patientId) {
		Payment payment = new Payment();
		payment.setInvoice(invoice);
		payment.setAppointmentId(invoice.getAppointmentId());
		payment.setPatientId(patientId);
		payment.setAmount(invoice.getTotalAmount());
		payment.setPaymentStatus(PaymentStatus.PENDING);
		paymentRepository.save(payment);
	}

	//Circuit Breaker methods for all the external service calls in this class
	@CircuitBreaker(name = "patientServiceCB", fallbackMethod = "getPatientInfoFallback")
	private PatientDTO getPatientInfo(Long patientId) {
		String roles = "ADMIN,USER,RECEPTIONIST";
		System.out.println("Patient ID in getPatientInfo: " + patientId);
		return patientClient.getPatientById(roles, patientId).getData();
	}

	private PatientDTO getPatientInfoFallback(Long patientId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered: " + t.getMessage());
		return PatientDTO.builder().id(patientId).fullName("Unknown Patient").build();
	}

	@CircuitBreaker(name = "patientServiceCB", fallbackMethod = "getAppointmentInfoFallback")
	private AppointmentDTO getAppointmentInfo(Long appointmentId) {
		String roles = "ADMIN,USER,RECEPTIONIST";
		return patientClient.getAppointmentById(roles, appointmentId).getData();
	}

	private AppointmentDTO getAppointmentInfoFallback(Long appointmentId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for appointment: " + t.getMessage());
		return AppointmentDTO.builder().id(appointmentId).build();
	}

	@CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "getDoctorInfoFallback")
	private DoctorDTO getDoctorInfo(Long doctorId) {
		String roles = "ADMIN,USER,RECEPTIONIST";
		System.out.println("Doctor ID in getDoctorInfo: " + doctorId);
		return doctorClient.getDoctorById(roles, doctorId);
	}

	private DoctorDTO getDoctorInfoFallback(Long doctorId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for doctor: " + t.getMessage());
		return DoctorDTO.builder().id(doctorId).fullName("Unknown Doctor").build();
	}

	@CircuitBreaker(name = "pharmacyServiceCB", fallbackMethod = "getMedicinesByAppointmentIdFallback")
	private List<PharmacyDTO> getMedicinesByAppointmentId(Long appointmentId) {
		String roles = "ADMIN,USER,RECEPTIONIST,PHARMACIST";
		return pharmacyClient.getMedicinesByAppointmentId(roles, appointmentId);
	}

	private List<PharmacyDTO> getMedicinesByAppointmentIdFallback(Long appointmentId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for medicines: " + t.getMessage());
		return List.of();
	}

	@CircuitBreaker(name = "labServiceCB", fallbackMethod = "getLabTestsByAppointmentIdFallback")
	private List<LabDTO> getLabTestsByAppointmentId(Long appointmentId) {
		String roles = "ADMIN,USER,RECEPTIONIST,LAB_TECHNICIAN";
		return labClient.getLabTestsByAppointmentId(roles, appointmentId).getData();
	}

	private List<LabDTO> getLabTestsByAppointmentIdFallback(Long appointmentId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for lab tests: " + t.getMessage());
		return List.of();
	}

	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "createNotificationFallback")
	private NotificationDTO createNotification(NotificationDTO notification) {
		notificationClient.send(notification);
		return notification;
	}

	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "createNotificationFallback")
	private List<NotificationDTO> getNotification(Long id) {
		return notificationClient.getAll(id);
	}

	private NotificationDTO createNotificationFallback(NotificationDTO notification, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for notification: " + t.getMessage());
		return notification;
	}
}
