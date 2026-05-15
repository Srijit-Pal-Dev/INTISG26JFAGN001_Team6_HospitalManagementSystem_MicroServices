package com.cognizant.patientService.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.cognizant.patientService.client.BillingServiceClient;
import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.*;
import com.cognizant.patientService.dto.AppointmentDTO;
import com.cognizant.patientService.dto.InvoiceDTO;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.exception.ResourceNotFoundException;
import com.cognizant.patientService.repository.AppointmentRepository;
import com.cognizant.patientService.repository.DoctorSlotRepository;
import com.cognizant.patientService.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private BillingServiceClient billingClient;

	@Mock
	private NotificationServiceClient notificationClient;

	@Mock
	private DoctorSlotRepository doctorSlotRepository;

	@InjectMocks
	private AppointmentServiceImpl appointmentService;

	private Patient patient;
	private DoctorSlot slot;
	private Appointment appointment;
	private AppointmentDTO appointmentDTO;

	@BeforeEach
	void setUp() {
		patient =
			Patient
				.builder()
				.id(1L)
				.userId(10L)
				.mrn("MRN001")
				.fullName("John Doe")
				.dob(LocalDate.of(1990, 1, 1))
				.gender("Male")
				.bloodGroup("O+")
				.phoneNo("1234567890")
				.address("123 Main St")
				.build();

		slot =
			DoctorSlot
				.builder()
				.id(1L)
				.doctorId(5L)
				.slotDate(LocalDate.of(2026, 5, 1))
				.slotTime(LocalTime.of(10, 0))
				.booked(false)
				.build();

		appointment =
			Appointment
				.builder()
				.id(1L)
				.doctorId(5L)
				.slotId(1L)
				.reason("Checkup")
				.status(Status.SCHEDULED)
				.appointmentDate(LocalDate.of(2026, 5, 1))
				.appointmentTime(LocalTime.of(10, 0))
				.patient(patient)
				.build();

		appointmentDTO = AppointmentDTO.builder().patientId(1L).slotId(1L).reason("Checkup").build();
	}

	@Test
	void scheduleAppointment_Success() {
		when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
		when(doctorSlotRepository.save(any(DoctorSlot.class))).thenReturn(slot);

		AppointmentDTO result = appointmentService.scheduleAppointment(10L, appointmentDTO);

		assertNotNull(result);
		assertEquals(Status.SCHEDULED, result.getStatus());
		verify(doctorSlotRepository).save(any(DoctorSlot.class));
		verify(notificationClient).send(any(NotificationDTO.class));
	}

	@Test
	void scheduleAppointment_PatientNotFound() {
		when(patientRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.scheduleAppointment(10L, appointmentDTO));
	}

	@Test
	void scheduleAppointment_SlotNotFound() {
		when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.scheduleAppointment(10L, appointmentDTO));
	}

	@Test
	void scheduleAppointment_SlotAlreadyBooked() {
		slot.setBooked(true);
		when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

		RuntimeException ex = assertThrows(
			RuntimeException.class,
			() -> appointmentService.scheduleAppointment(10L, appointmentDTO)
		);
		assertTrue(ex.getMessage().contains("already occupied"));
	}

	@Test
	void updateAppointment_Success_SameSlot() {
		appointmentDTO.setSlotId(1L); // same slot
		appointmentDTO.setReason("Updated reason");
		appointmentDTO.setStatus(Status.SCHEDULED);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

		AppointmentDTO result = appointmentService.updateAppointment(1L, appointmentDTO);

		assertNotNull(result);
		verify(appointmentRepository).save(any(Appointment.class));
	}

	@Test
	void updateAppointment_ChangeSlot() {
		DoctorSlot newSlot = DoctorSlot
			.builder()
			.id(2L)
			.doctorId(6L)
			.slotDate(LocalDate.of(2026, 5, 2))
			.slotTime(LocalTime.of(11, 0))
			.booked(false)
			.build();

		appointmentDTO.setSlotId(2L);
		appointmentDTO.setReason("New reason");

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
		when(doctorSlotRepository.findById(2L)).thenReturn(Optional.of(newSlot));
		when(doctorSlotRepository.save(any(DoctorSlot.class))).thenAnswer(i -> i.getArgument(0));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

		AppointmentDTO result = appointmentService.updateAppointment(1L, appointmentDTO);

		assertNotNull(result);
		verify(doctorSlotRepository, times(2)).save(any(DoctorSlot.class));
	}

	@Test
	void updateAppointment_ChangeSlot_NewSlotBooked() {
		DoctorSlot newSlot = DoctorSlot
			.builder()
			.id(2L)
			.doctorId(6L)
			.slotDate(LocalDate.of(2026, 5, 2))
			.slotTime(LocalTime.of(11, 0))
			.booked(true)
			.build();

		appointmentDTO.setSlotId(2L);

		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
		when(doctorSlotRepository.findById(2L)).thenReturn(Optional.of(newSlot));
		when(doctorSlotRepository.save(any(DoctorSlot.class))).thenAnswer(i -> i.getArgument(0));

		assertThrows(RuntimeException.class, () -> appointmentService.updateAppointment(1L, appointmentDTO));
	}

	@Test
	void updateAppointment_NotFound() {
		when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.updateAppointment(99L, appointmentDTO));
	}

	@Test
	void getAppointmentById_Success() {
		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

		AppointmentDTO result = appointmentService.getAppointmentById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	void getAppointmentById_NotFound() {
		when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentById(99L));
	}

	@Test
	void getAppointmentByPatientId_NotFound() {
		when(appointmentRepository.findByPatientId(99L)).thenReturn(Collections.emptyList());

		assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentByPatientId(99L));
	}

	@Test
	void getAppointmentByDoctorId_Success() {
		when(appointmentRepository.findByDoctorId(5L)).thenReturn(Optional.of(appointment));

		AppointmentDTO result = appointmentService.getAppointmentByDoctorId(5L);

		assertNotNull(result);
	}

	@Test
	void getAppointmentByDoctorId_NotFound() {
		when(appointmentRepository.findByDoctorId(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentByDoctorId(99L));
	}

	@Test
	void getAppointmentByStatus_Success() {
		when(appointmentRepository.findByStatus(Status.SCHEDULED)).thenReturn(Optional.of(appointment));

		AppointmentDTO result = appointmentService.getAppointmentByStatus(Status.SCHEDULED);

		assertNotNull(result);
		assertEquals(Status.SCHEDULED, result.getStatus());
	}

	@Test
	void getAppointmentByStatus_NotFound() {
		when(appointmentRepository.findByStatus(Status.CANCELLED)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.getAppointmentByStatus(Status.CANCELLED));
	}

	@Test
	void getAllAppointments_Success() {
		when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

		List<AppointmentDTO> result = appointmentService.getAllAppointments();

		assertEquals(1, result.size());
	}

	@Test
	void getAllAppointments_Empty() {
		when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

		List<AppointmentDTO> result = appointmentService.getAllAppointments();

		assertTrue(result.isEmpty());
	}

	@Test
	void deleteAppointment_Success() {
		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(doctorSlotRepository.findById(1L)).thenReturn(Optional.of(slot));

		appointmentService.deleteAppointment(1L);

		verify(appointmentRepository).deleteById(1L);
		verify(doctorSlotRepository).save(any(DoctorSlot.class));
		verify(notificationClient).send(any(NotificationDTO.class));
	}

	@Test
	void deleteAppointment_NotFound() {
		when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.deleteAppointment(99L));
	}

	@Test
	void completeAppointment_Success() {
		when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
		when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
		when(billingClient.initiateInvoice(1L, 1L)).thenReturn(new InvoiceDTO());

		AppointmentDTO result = appointmentService.completeAppointment(1L);

		assertNotNull(result);
		assertEquals(Status.COMPLETED, appointment.getStatus());
		verify(billingClient).initiateInvoice(1L, 1L);
	}

	@Test
	void completeAppointment_NotFound() {
		when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> appointmentService.completeAppointment(99L));
	}
}
