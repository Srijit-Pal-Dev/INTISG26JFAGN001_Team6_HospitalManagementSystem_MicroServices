package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.domain.Prescription;
import com.cognizant.prescriptionservice.dto.CreatePrescriptionRequest;
import com.cognizant.prescriptionservice.dto.PrescriptionResponse;
import com.cognizant.prescriptionservice.exception.ResourceNotFoundException;
import com.cognizant.prescriptionservice.mapper.PrescriptionMapper;
import com.cognizant.prescriptionservice.repository.DoctorRepository;
import com.cognizant.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

	private final PrescriptionRepository prescriptionRepository;
	private final DoctorRepository doctorRepository;

	@Override
	@Transactional
	public PrescriptionResponse createPrescription(Long userId, CreatePrescriptionRequest request) {
		System.out.println(">>> createPrescription called with userId=" + userId);

		Doctor doctor = doctorRepository
			.findByUserId(userId)
			.orElseGet(() -> {
				System.out.println(">>> Doctor not found by userId=" + userId + ", trying findById...");
				return doctorRepository
					.findById(userId)
					.orElseThrow(() ->
						new ResourceNotFoundException(
							"Doctor profile not found for userId=" +
							userId +
							". Ensure the doctor has created their profile via /doctors/profile/create"
						)
					);
			});

		Prescription prescription = PrescriptionMapper.toEntity(request, doctor);

		Prescription saved = prescriptionRepository.save(prescription);

		return PrescriptionMapper.toResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponse getPrescriptionById(Long id) {
		Prescription prescription = prescriptionRepository
			.findDetailedById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

		return PrescriptionMapper.toResponse(prescription);
	}

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId) {
		Prescription prescription = prescriptionRepository
			.findByAppointmentId(appointmentId)
			.orElseThrow(() -> new ResourceNotFoundException("Prescription not found for appointment"));

		return PrescriptionMapper.toResponse(prescription);
	}
}
