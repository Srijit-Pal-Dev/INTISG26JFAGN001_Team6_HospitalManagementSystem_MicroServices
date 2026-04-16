package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.client.AppointmentServiceClient;
import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import com.cognizant.prescriptionservice.repository.DoctorRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorSlotServiceImpl implements DoctorSlotService {

	private final AppointmentServiceClient appointmentServiceClient;
	private final DoctorRepository doctorRepository;

	@Override
    @Transactional
	public void createSlot(DoctorSlotRequest slot) {
		Doctor doctor = doctorRepository
			.findById(slot.getDoctorId())
			.orElseThrow(() -> new RuntimeException("Doctor not found with id: " + slot.getDoctorId()));
		slot.setUserId(doctor.getUserId());
		appointmentServiceClient.createSlot("DOCTOR", slot);
	}

	@Override
    @Transactional
	public void addDoctorSlots(List<DoctorSlotRequest> slots) {
		for (DoctorSlotRequest slot : slots) {
			createSlot(slot);
		}
	}
}
