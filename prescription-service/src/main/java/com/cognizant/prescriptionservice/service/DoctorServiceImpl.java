package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import com.cognizant.prescriptionservice.exception.ResourceNotFoundException;
import com.cognizant.prescriptionservice.mapper.DoctorMapper;
import com.cognizant.prescriptionservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepository;

	@Override
	public DoctorResponse createDoctorProfile(DoctorProfileRequest request, Long userId) {
		request.setUserId(userId);
		Doctor newDoctor = DoctorMapper.toEntity(request);
		Doctor savedDoctor = doctorRepository.save(newDoctor);
		return DoctorMapper.toDTO(savedDoctor);
	}

	@Override
	public DoctorResponse getDoctorProfile(Long userId) {
		Doctor doctor = doctorRepository
			.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

		return DoctorMapper.toDTO(doctor);
	}

	@Override
	public DoctorResponse updateDoctorProfile(Long userId, DoctorProfileRequest request) {
		Doctor doctor = doctorRepository
			.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

		DoctorMapper.toEntity(request);
		return DoctorMapper.toDTO(doctorRepository.save(doctor));
	}

	@Override
    public DoctorResponse getDoctorById(Long doctorId) {
		Doctor doctor = doctorRepository
			.findById(doctorId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
		return DoctorMapper.toDTO(doctor);
	}
}
