package com.cognizant.patientService.service;

import com.cognizant.patientService.dto.DoctorSlotDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorSlotService {
	DoctorSlotDTO createSlot(DoctorSlotDTO doctorSlotDTO, Long userId);

	List<DoctorSlotDTO> createManySlots(
		Long userId,
		Long doctorId,
		LocalDate slotDate,
		LocalTime startTime,
		int numberOfSlots,
		int slotMinutes
	);

	DoctorSlotDTO updateSlot(Long id, DoctorSlotDTO doctorSlotDTO);

	DoctorSlotDTO getSlotById(Long id);

	List<DoctorSlotDTO> getSlotByDoctorId(Long id);

	List<DoctorSlotDTO> getAllSlots();

	void deleteSlot(Long id);
}
