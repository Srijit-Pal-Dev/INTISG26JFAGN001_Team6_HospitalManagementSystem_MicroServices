package com.cognizant.patientService.service;

import com.cognizant.patientService.client.BillingServiceClient;
import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.Appointment;
import com.cognizant.patientService.domain.DoctorSlot;
import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.dto.InvoiceDTO;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.dto.NotificationType;
import com.cognizant.patientService.exception.ResourceNotFoundException;
import com.cognizant.patientService.mapper.AppointmentMapper;
import com.cognizant.patientService.repository.AppointmentRepository;
import com.cognizant.patientService.repository.DoctorSlotRepository;
import com.cognizant.patientService.repository.PatientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final BillingServiceClient billingClient;
	private final NotificationServiceClient notificationClient;
	private final DoctorSlotRepository doctorSlotRepository;

	public AppointmentServiceImpl(
		AppointmentRepository appointmentRepository,
		PatientRepository patientRepository,
		BillingServiceClient billingClient,
		NotificationServiceClient notificationClient,
		DoctorSlotRepository doctorSlotRepository
	) {
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.billingClient = billingClient;
		this.notificationClient = notificationClient;
		this.doctorSlotRepository = doctorSlotRepository;
	}

	/* this method schedules a new appointment for a patient. It first checks if the
     patient exists, then creates an appointment entity from the provided DTO, saves
     it to the database, and sends a notification to the patient about the scheduled
     appointment. Finally, it returns the saved appointment as a DTO. */
	@Override
	@Transactional
	public AppointmentDTO scheduleAppointment(Long userId, AppointmentDTO appointmentDTO) {
		Patient patient = patientRepository
			.findById(appointmentDTO.getPatientId())
			.orElseThrow(() -> new RuntimeException("Patient with id " + appointmentDTO.getPatientId() + " not found"));
		DoctorSlot slot = doctorSlotRepository
			.findById(appointmentDTO.getSlotId())
			.orElseThrow(() -> new RuntimeException("Doctor slot with id " + appointmentDTO.getSlotId() + " not found")
			);

		// Check if slot is already booked
		if (slot.isBooked()) {
			throw new RuntimeException(
				"Doctor is already occupied at " +
				slot.getSlotDate() +
				" " +
				slot.getSlotTime() +
				". Please choose a different slot."
			);
		}

		// Autopopulate date, time and doctorId from the slot
		appointmentDTO.setDoctorId(slot.getDoctorId());
		appointmentDTO.setAppointmentDate(slot.getSlotDate());
		appointmentDTO.setAppointmentTime(slot.getSlotTime());
		appointmentDTO.setStatus(Status.SCHEDULED);

		Appointment appointment = AppointmentMapper.toEntity(appointmentDTO, patient);
		Appointment savedAppointment = appointmentRepository.save(appointment);

		// Mark slot as booked
		slot.setBooked(true);
		doctorSlotRepository.save(slot);

		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(userId)
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
	@Transactional
	public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));

		// If slot is being changed, free old slot and book new one
		if (appointmentDTO.getSlotId() != null && !appointmentDTO.getSlotId().equals(appointment.getSlotId())) {
			// Free old slot
			if (appointment.getSlotId() != null) {
				doctorSlotRepository
					.findById(appointment.getSlotId())
					.ifPresent(oldSlot -> {
						oldSlot.setBooked(false);
						doctorSlotRepository.save(oldSlot);
					});
			}
			// Book new slot
			DoctorSlot newSlot = doctorSlotRepository
				.findById(appointmentDTO.getSlotId())
				.orElseThrow(() ->
					new RuntimeException("Doctor slot with id " + appointmentDTO.getSlotId() + " not found")
				);
			if (newSlot.isBooked()) {
				throw new RuntimeException(
					"Doctor is already occupied at " +
					newSlot.getSlotDate() +
					" " +
					newSlot.getSlotTime() +
					". Please choose a different slot."
				);
			}
			newSlot.setBooked(true);
			doctorSlotRepository.save(newSlot);

			appointment.setSlotId(appointmentDTO.getSlotId());
			appointment.setDoctorId(newSlot.getDoctorId());
			appointment.setAppointmentDate(newSlot.getSlotDate());
			appointment.setAppointmentTime(newSlot.getSlotTime());
		}

		appointment.setReason(appointmentDTO.getReason());
		if (appointmentDTO.getStatus() != null) {
			appointment.setStatus(appointmentDTO.getStatus());
		}

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
	@Transactional
	public AppointmentDTO getAppointmentById(Long id) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves an appointment by the patient ID. It checks if the appointment exists for the given
     patient ID and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public List<AppointmentDTO> getAppointmentByPatientId(Long patientId) {
		List<Appointment> appointment = appointmentRepository.findByPatientId(patientId);
		if (appointment.isEmpty()) {
			throw new ResourceNotFoundException("Appointments not found");
		}
		return appointment.stream().map(AppointmentMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves an appointment by the doctor ID. It checks if the appointment exists for the given
     doctor ID and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public AppointmentDTO getAppointmentByDoctorId(Long doctorId) {
		Appointment appointment = appointmentRepository
			.findByDoctorId(doctorId)
			.orElseThrow(() -> new RuntimeException("Appointment with doctor id " + doctorId + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves an appointment by its status. It checks if the appointment exists for the given
     status and returns its DTO. If the appointment is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public AppointmentDTO getAppointmentByStatus(Status status) {
		Appointment appointment = appointmentRepository
			.findByStatus(status)
			.orElseThrow(() -> new RuntimeException("Appointment with status " + status + " not found"));

		return AppointmentMapper.toDTO(appointment);
	}

	/* this method retrieves all appointments from the database, converts them
     to DTOs, and returns the list of appointment DTOs. */
	@Override
	@Transactional
	public List<AppointmentDTO> getAllAppointments() {
		List<Appointment> appointment = appointmentRepository.findAll();

		return appointment.stream().map(AppointmentMapper::toDTO).collect(Collectors.toList());
	}

	/* this method deletes an appointment by its ID. It first checks if the appointment exists, then deletes it from the database,
     and sends a notification to the patient about the cancellation. If the appointment is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public void deleteAppointment(Long id) {
		Appointment appointment = appointmentRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Appointment with " + id + " not found"));
		Patient patient = appointment.getPatient();

		// Free the slot so it can be booked again
		if (appointment.getSlotId() != null) {
			doctorSlotRepository
				.findById(appointment.getSlotId())
				.ifPresent(slot -> {
					slot.setBooked(false);
					doctorSlotRepository.save(slot);
				});
		}

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
	@Transactional
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
