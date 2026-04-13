package com.cognizant.patientService.service;

import com.cognizant.patientService.client.BillingServiceClient;
import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.Appointment;
import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.dto.InvoiceDTO;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.dto.NotificationType;
import com.cognizant.patientService.mapper.AppointmentMapper;
import com.cognizant.patientService.repository.AppointmentRepository;
import com.cognizant.patientService.repository.PatientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final BillingServiceClient billingClient;
	private final NotificationServiceClient notificationClient;

	public AppointmentServiceImpl(
		AppointmentRepository appointmentRepository,
		PatientRepository patientRepository,
		BillingServiceClient billingClient,
		NotificationServiceClient notificationClient
	) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.billingClient = billingClient;
		this.notificationClient = notificationClient;
	}

	/* this method schedules a new appointment for a patient. It first checks if the
     patient exists, then creates an appointment entity from the provided DTO, saves
     it to the database, and sends a notification to the patient about the scheduled
     appointment. Finally, it returns the saved appointment as a DTO. */
	@Override
	public AppointmentDTO scheduleAppointment(AppointmentDTO appointmentDTO) {
		Patient patient = patientRepository
			.findById(appointmentDTO.getPatientId())
			.orElseThrow(() -> new RuntimeException("Patient with id " + appointmentDTO.getPatientId() + " not found"));
		Appointment appointment = AppointmentMapper.toEntity(appointmentDTO, patient);
		Appointment savedAppointment = appointmentRepository.save(appointment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(patient.getUserId())
			.title("Appointment Scheduled")
			.message(
				"Your appointment with doctor id " +
				appointment.getDoctorId() +
				" has been scheduled for " +
				appointment.getAppointmentDate() +
				" at " +
				appointment.getAppointmentTime()
			)
			.type(NotificationType.APPOINTMENT)
			.build();
		createNotification(notification);
		return AppointmentMapper.toDTO(savedAppointment);
	}

	/* this method updates an existing appointment. It first checks if the appointment exists,
     then updates the appointment entity with the new details from the provided DTO, saves
     the updated appointment to the database, and sends a notification to the patient about
     the updated appointment. Finally, it returns the updated appointment as a DTO. */
	@Override
	public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));

		appointment.setDoctorId(appointmentDTO.getDoctorId());
		appointment.setSlotId(appointmentDTO.getSlotId());
		appointment.setReason(appointmentDTO.getReason());
		appointment.setStatus(appointmentDTO.getStatus());
		appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
		appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());

		Appointment updated = appointmentRepository.save(appointment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(appointment.getPatient().getUserId())
			.title("Appointment Updated")
			.message(
				"Your appointment with doctor id " +
				appointment.getDoctorId() +
				" has been updated for " +
				appointment.getAppointmentDate() +
				" at " +
				appointment.getAppointmentTime()
			)
			.type(NotificationType.APPOINTMENT)
			.build();
		createNotification(notification);

		return AppointmentMapper.toDTO(updated);
	}

	/*this method retrieves an appointment by its ID. It checks if the appointment exists and returns
     its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	public AppointmentDTO getAppointmentById(Long id) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves an appointment by the patient ID. It checks if the appointment exists for the given
     patient ID and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	public AppointmentDTO getAppointmentByPatientId(Long patientId) {
		Appointment appointment = appointmentRepository
			.findByPatientId(patientId)
			.orElseThrow(() -> new RuntimeException("Appointment with patient id " + patientId + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves an appointment by the doctor ID. It checks if the appointment exists for the given
     doctor ID and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	public AppointmentDTO getAppointmentByDoctorId(Long doctorId) {
		Appointment appointment = appointmentRepository
			.findByDoctorId(doctorId)
			.orElseThrow(() -> new RuntimeException("Appointment with doctor id " + doctorId + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves an appointment by its status. It checks if the appointment exists for the given
     status and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	public AppointmentDTO getAppointmentByStatus(Status status) {
		Appointment appointment = appointmentRepository
			.findByStatus(status)
			.orElseThrow(() -> new RuntimeException("Appointment with status " + status + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves all appointments from the database, converts them
     to DTOs, and returns the list of appointment DTOs. */
	@Override
	public List<AppointmentDTO> getAllAppointments() {
		List<Appointment> appointment = appointmentRepository.findAll();

		return appointment.stream().map(AppointmentMapper::toDTO).collect(Collectors.toList());
	}

	/* this method deletes an appointment by its ID. It first checks if the appointment exists, then deletes it from the database,
     and sends a notification to the patient about the cancellation. If the appointment is not found, it throws a RuntimeException. */
	@Override
	public void deleteAppointment(Long id) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));
		Patient patient = appointment.getPatient();
		appointmentRepository.deleteById(id);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(patient.getUserId())
			.title("Appointment Cancelled")
			.message(
				"Your appointment with doctor id " +
				appointment.getDoctorId() +
				" has been cancelled for " +
				appointment.getAppointmentDate() +
				" at " +
				appointment.getAppointmentTime()
			)
			.type(NotificationType.APPOINTMENT)
			.build();
		createNotification(notification);
	}

	/* this method marks an appointment as completed. It first checks if the appointment exists, then updates its status to COMPLETED,
     saves the updated appointment to the database, sends a notification to the patient about the completion, and triggers the billing
      service to create an invoice for the appointment. Finally, it returns the completed appointment as a DTO. If the appointment is
      not found, it throws a RuntimeException. */
	@Override
	public AppointmentDTO completeAppointment(Long appointmentId) {
		Appointment appointment = appointmentRepository
			.findById(appointmentId)
			.orElseThrow(() -> new RuntimeException("Appointment not found"));

		//Update status
		appointment.setStatus(Status.COMPLETED);
		appointmentRepository.save(appointment);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(appointment.getPatient().getUserId())
			.title("Appointment Completed")
			.message(
				"Your appointment with doctor id " +
				appointment.getDoctorId() +
				" is completed at " +
				appointment.getUpdatedAt()
			)
			.type(NotificationType.APPOINTMENT)
			.build();
		createNotification(notification);

		// Trigger Billing Service
		createInvoice(appointment.getPatient().getId(), appointment.getId());

		return AppointmentMapper.toDTO(appointment);
	}

	/* Circuit breaker methods for notification and billing service calls. If the external service is down or fails,
    the fallback methods will be triggered to handle the failure gracefully. The fallback methods log the error and
    return a default response. */
	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "createNotificationFallback")
	private NotificationDTO createNotification(NotificationDTO notification) {
		notificationClient.send(notification);
		return notification;
	}

	private NotificationDTO createNotificationFallback(NotificationDTO notification, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for notification: " + t.getMessage());
		return notification;
	}

	@CircuitBreaker(name = "billingServiceCB", fallbackMethod = "initiateInvoiceFallback")
	private InvoiceDTO createInvoice(Long patientId, Long appointmentId) {
		return billingClient.initiateInvoice(patientId, appointmentId);
	}

	private InvoiceDTO initiateInvoiceFallback(Long patientId, Long appointmentId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for billing service: " + t.getMessage());
		return null;
	}
}
