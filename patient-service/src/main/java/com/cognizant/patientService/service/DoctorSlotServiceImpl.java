package com.cognizant.patientService.service;

import com.cognizant.patientService.client.NotificationServiceClient;
import com.cognizant.patientService.domain.DoctorSlot;
import com.cognizant.patientService.dto.DoctorSlotDTO;
import com.cognizant.patientService.dto.NotificationDTO;
import com.cognizant.patientService.dto.NotificationType;
import com.cognizant.patientService.exception.SlotAlreadyExistsException;
import com.cognizant.patientService.mapper.DoctorSlotMapper;
import com.cognizant.patientService.repository.DoctorSlotRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorSlotServiceImpl implements DoctorSlotService {

	private final DoctorSlotRepository doctorSlotRepository;
	private final NotificationServiceClient notificationClient;

	public DoctorSlotServiceImpl(
		DoctorSlotRepository doctorSlotRepository,
		NotificationServiceClient notificationClient
	) {
		this.doctorSlotRepository = doctorSlotRepository;
		this.notificationClient = notificationClient;
	}

	/* this method creates a doctor slot. It first checks if a slot already exists for the given doctor, date, and time.
    If a slot already exists, it throws a SlotAlreadyExistsException. If the slot does not exist, it converts the
    provided DoctorSlotDTO to a DoctorSlot entity, saves it to the database, and sends a notification to the user
    associated with the slot, informing them about the creation of the new slot. Finally, it returns the created slot
    as a DoctorSlotDTO. */
	@Override
	@Transactional
	public DoctorSlotDTO createSlot(DoctorSlotDTO doctorSlotDTO, Long userId) {
		Optional<DoctorSlot> existing = doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(
			doctorSlotDTO.getDoctorId(),
			doctorSlotDTO.getSlotDate(),
			doctorSlotDTO.getSlotTime()
		);

		if (existing.isPresent()) {
			throw new SlotAlreadyExistsException("Slot already created for the given date and time");
		}
		doctorSlotDTO.setUserId(userId);
		System.out.println("Creating DoctorSlot with data: " + doctorSlotDTO);
		DoctorSlot slot = DoctorSlotMapper.toEntity(doctorSlotDTO);
		System.out.println("DoctorSlot entity created: " + slot);
		DoctorSlot saved = doctorSlotRepository.save(slot);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(userId)
			.title("Slot Created")
			.message(
				"A new slot has been created for doctor id " +
				saved.getDoctorId() +
				" on " +
				saved.getSlotDate() +
				" at " +
				saved.getSlotTime()
			)
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);
		return DoctorSlotMapper.toDTO(saved);
	}

	/* this method creates multiple doctor slots for a given doctor on a specific date, starting from a specified time.
    It generates the slots based on the number of slots and the duration of each slot in minutes. For each generated
    slot, it checks if a slot already exists for the doctor at the given date and time. If a slot already exists,
    it throws a SlotAlreadyExistsException. If the slot does not exist, it creates a new DoctorSlotDTO, saves it to the
    database, and sends a notification to the user associated with the slot, informing them about the creation of the
    new slot. Finally, it returns a list of the created doctor slot DTOs. */
	@Override
	@Transactional
	public List<DoctorSlotDTO> createManySlots(
            Long userId,
		Long doctorId,
		LocalDate slotDate,
		LocalTime startTime,
		int numberOfSlots,
		int slotMinutes
	) {
		List<DoctorSlotDTO> slots = new ArrayList<>();

		for (int i = 0; i < numberOfSlots; i++) {
			LocalTime slotStart = startTime.plusMinutes(i * slotMinutes);
			//check if slots already exist for doctor at given date and time
			Optional<DoctorSlot> existing = doctorSlotRepository.findByDoctorIdAndSlotDateAndSlotTime(
				doctorId,
				slotDate,
				slotStart
			);
			if (existing.isPresent()) {
				throw new SlotAlreadyExistsException("Slot already created for the given date and time");
			}
			DoctorSlotDTO dto = new DoctorSlotDTO();
			dto.setDoctorId(doctorId);
			dto.setSlotDate(slotDate);
			dto.setSlotTime(slotStart);
			dto.setBooked(false);

			// save entity
			DoctorSlot entity = DoctorSlotMapper.toEntity(dto);
			DoctorSlot saved = doctorSlotRepository.save(entity);
			NotificationDTO notification = NotificationDTO
				.builder()
				.userId(userId)
				.title("Slot Created")
				.message(
					"A new slot has been created for doctor id " +
					saved.getDoctorId() +
					" on " +
					saved.getSlotDate() +
					" at " +
					saved.getSlotTime()
				)
				.type(NotificationType.GENERAL)
				.build();
			createNotification(notification);

			slots.add(DoctorSlotMapper.toDTO(saved));
		}
		return slots;
	}

	/* this method updates an existing doctor slot. It first retrieves the slot by its ID, then updates
    its properties based on the provided DTO, saves the updated slot to the database, and sends a notification
    to the user associated with the slot, informing them about the update. Finally, it returns the updated slot
    as a DTO. If the slot is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public DoctorSlotDTO updateSlot(Long id, DoctorSlotDTO doctorSlotDTO) {
		DoctorSlot slot = doctorSlotRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Doctor Slot with " + id + " not found"));

		slot.setDoctorId(doctorSlotDTO.getDoctorId());
		slot.setSlotDate(doctorSlotDTO.getSlotDate());
		slot.setSlotTime(doctorSlotDTO.getSlotTime());
		slot.setBooked(doctorSlotDTO.isBooked());

		DoctorSlot updated = doctorSlotRepository.save(slot);
		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(updated.getUserId())
			.title("Slot Created")
			.message(
				"A new slot has been created for doctor id " +
				updated.getDoctorId() +
				" on " +
				updated.getSlotDate() +
				" at " +
				updated.getSlotTime()
			)
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);
		return DoctorSlotMapper.toDTO(updated);
	}

	/* this method retrieves a doctor slot by its ID. It checks if the slot exists and returns its DTO.
    If the slot is not found, it throws a RuntimeException. */
	@Override
	@Transactional
	public DoctorSlotDTO getSlotById(Long id) {
		DoctorSlot slot = doctorSlotRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Doctor Slot with " + id + " not found"));

		return DoctorSlotMapper.toDTO(slot);
	}

	/* this method retrieves all doctor slots for a specific doctor by their ID. It queries the database for slots
    associated with the given doctor ID, converts each slot entity to a DTO using the DoctorSlotMapper, and returns
    the list of doctor slot DTOs. If no slots are found for the doctor, it will return an empty list. */
	@Override
	@Transactional
	public List<DoctorSlotDTO> getSlotByDoctorId(Long id) {
		List<DoctorSlot> slots = doctorSlotRepository.findByDoctorId(id);

		return slots.stream().map(DoctorSlotMapper::toDTO).collect(Collectors.toList());
	}

	/* this method retrieves all doctor slots from the database, converts them to DTOs,
    and returns the list of doctor slot DTOs. */
	@Override
	@Transactional
	public List<DoctorSlotDTO> getAllSlots() {
		List<DoctorSlot> slots = doctorSlotRepository.findAll();
		return slots.stream().map(DoctorSlotMapper::toDTO).collect(Collectors.toList());
	}

	/* this method deletes a doctor slot by its ID. It first checks if the slot exists and retrieves it. If the slot is
     not found, it throws a RuntimeException. If the slot exists, it deletes it from the database and sends a notification
     to the user associated with the slot, informing them about the deletion. The notification includes details about the
     doctor, date, and time of the deleted slot. */
	@Override
	@Transactional
	public void deleteSlot(Long id) {
		DoctorSlot slot = doctorSlotRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Doctor Slot with " + id + " not found"));

		doctorSlotRepository.deleteById(id);

		NotificationDTO notification = NotificationDTO
			.builder()
			.userId(slot.getUserId())
			.title("Slot Deleted")
			.message(
				"A slot has been deleted for doctor id " +
				slot.getDoctorId() +
				" on " +
				slot.getSlotDate() +
				" at " +
				slot.getSlotTime()
			)
			.type(NotificationType.GENERAL)
			.build();
		createNotification(notification);
	}

	/* this method sends a notification to the notification service using the NotificationServiceClient.
    It is annotated with @CircuitBreaker to handle failures gracefully. If the notification service is down
    or fails to respond, the fallback method createNotificationFallback will be triggered, which logs the
    error and returns the original notification without sending it. */
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
