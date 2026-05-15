package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import com.cognizant.prescriptionservice.exception.ResourceNotFoundException;
import com.cognizant.prescriptionservice.mapper.DoctorMapper;
import com.cognizant.prescriptionservice.repository.DoctorRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepository;

	@Override
	@Transactional
	public DoctorResponse createDoctorProfile(DoctorProfileRequest request, Long userId) {
		request.setUserId(userId);
		Doctor newDoctor = DoctorMapper.toEntity(request);
		Doctor savedDoctor = doctorRepository.save(newDoctor);
		return DoctorMapper.toDTO(savedDoctor);
	}

	@Override
	@Transactional
	public DoctorResponse getDoctorProfile(Long userId) {
		Doctor doctor = doctorRepository
			.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

		return DoctorMapper.toDTO(doctor);
	}

	@Override
	@Transactional
	public DoctorResponse updateDoctorProfile(Long userId, DoctorProfileRequest request) {
		Doctor doctor = doctorRepository
			.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));

		DoctorMapper.updateEntity(doctor, request);
		return DoctorMapper.toDTO(doctorRepository.save(doctor));
	}

	@Override
	@Transactional
	public DoctorResponse getDoctorById(Long doctorId) {
		Doctor doctor = doctorRepository
			.findById(doctorId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
		return DoctorMapper.toDTO(doctor);
	}

	@Transactional
	@Override
	public List<DoctorResponse> getAllDoctor() {
		List<Doctor> doctor = doctorRepository.findAll();
		if (doctor.isEmpty()) {
			new ResourceNotFoundException("Doctors not found");
		}
		return doctor.stream().map(DoctorMapper::toDTO).collect(Collectors.toList());
	}
}
