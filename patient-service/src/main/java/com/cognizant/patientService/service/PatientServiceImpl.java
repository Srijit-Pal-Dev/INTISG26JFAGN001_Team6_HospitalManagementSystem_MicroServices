package com.cognizant.patientService.service;

import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.dto.NotificationType;
import com.cognizant.patientService.dto.PatientDTO;
import com.cognizant.patientService.exception.ResourceNotFoundException;
import com.cognizant.patientService.mapper.PatientMapper;
import com.cognizant.patientService.repository.PatientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;
	private final NotificationServiceClient notificationClient;

	public PatientServiceImpl(PatientRepository patientRepository, NotificationServiceClient notificationClient) {
		this.patientRepository = patientRepository;
		this.notificationClient = notificationClient;
	}

	/* this method creates a new patient record. It takes a PatientDTO as input, converts it to a Patient entity,
    generates a unique medical record number (MRN) for the patient, saves the patient record to the database, and
    sends a notification to the user associated with the patient record, informing them that their patient record
    has been created. Finally, it returns the created patient record as a PatientDTO. */
	@Override
	@Transactional
	public PatientDTO createPatient(PatientDTO patientDTO) {
		Patient patient = PatientMapper.toEntity(patientDTO);

		Long count = patientRepository.count() + 1;
		String generatedMrn = "MRN" + String.format("%03d", count);

		patient.setMrn(generatedMrn);
		Patient saved = patientRepository.save(patient);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(saved.getUserId())
			.title("Patient Registered")
			.message(
				"Welcome " + saved.getFullName() + "! Your patient record has been created with MRN: " + saved.getMrn()
			)
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);

		return PatientMapper.toDto(saved);
	}

	/* this method updates an existing patient record. It first checks if the patient exists using the provided ID.
    If the patient is found, it updates the patient's details with the information from the PatientDTO, saves the
    updated patient record to the database, and sends a notification to the user associated with the patient record,
    informing them that their patient record has been updated. If the patient is not found, it throws a
    ResourceNotFoundException. Finally, it returns the updated patient record as a PatientDTO. */
	@Override
	@Transactional
	public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
		Patient patient = patientRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Patient with " + id + " not found"));

		patient.setFullName(patientDTO.getFullName());
		patient.setDob(patientDTO.getDob());
		patient.setGender(patientDTO.getGender());
		patient.setBloodGroup(patientDTO.getBloodGroup());
		patient.setPhoneNo(patientDTO.getPhoneNo());
		patient.setAddress(patientDTO.getAddress());

		Patient updated = patientRepository.save(patient);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(updated.getUserId())
			.title("Patient Record Updated")
			.message("Hello " + updated.getFullName() + "! Your patient record has been updated successfully.")
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);
		return PatientMapper.toDto(updated);
	}

	/* this method retrieves a patient record by its ID. It checks if the patient exists in the database
    using the provided ID and returns its DTO. If the patient is not found, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public PatientDTO getPatientById(Long id) {
		Patient patient = patientRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Patient with " + id + " not found"));

		return PatientMapper.toDto(patient);
	}

	/* this method retrieves a patient record by its medical record number (MRN). It checks if the patient
    exists in the database using the MRN and returns its DTO. If the patient is not found, it throws a
    ResourceNotFoundException. */
	@Override
	@Transactional
	public PatientDTO getPatientByMrn(String mrn) {
		Patient patient = patientRepository
			.findPatientByMrn(mrn)
			.orElseThrow(() -> new ResourceNotFoundException("Patient with " + mrn + " not found"));

		return PatientMapper.toDto(patient);
	}

	/* this method retrieves all patient records from the database. It checks if the list of patients
    is empty and throws a ResourceNotFoundException if no patients are found. If patients are found,
    it converts each patient entity to a PatientDTO using the PatientMapper and returns the list of PatientDTOs. */
	@Override
	@Transactional
	public List<PatientDTO> getAllPatient() {
		List<Patient> patientList = patientRepository.findAll();
		if (patientList.isEmpty()) {
			throw new ResourceNotFoundException("Patients not found");
		}
		return patientList.stream().map(PatientMapper::toDto).collect(Collectors.toList());
	}

	/* this method deletes a patient record by its ID. It first checks if the patient exists and then
    deletes it from the database. After deletion, it sends a notification to the user associated with
    the patient record, informing them that their patient record has been deleted. If the patient is
    not found, it throws a ResourceNotFoundException. */
	@Override
	@Transactional
	public void deletePatient(Long id) {
		Patient patient = patientRepository
			.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Patient with " + id + " not found"));
		patientRepository.deleteById(id);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(patient.getUserId())
			.title("Patient Record Deleted")
			.message(
				"Hello " +
				patient.getFullName() +
				"! Your patient record with MRN: " +
				patient.getMrn() +
				" has been deleted."
			)
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);
	}

	/* Circuit breaker method to send notifications. If the notification service is down or fails, it will trigger
    the fallback method. The fallback method logs the error and returns the original notification without sending it.
    This ensures that the patient service can continue to function even if the notification service is unavailable. */
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
