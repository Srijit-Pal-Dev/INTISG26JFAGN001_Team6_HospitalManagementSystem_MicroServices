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
    public DoctorResponse getDoctorProfile(Long userId) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        return DoctorMapper.toResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctorProfile(
            Long userId,
            DoctorProfileRequest request) {

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        DoctorMapper.updateDoctorFromRequest(request, doctor);
        return DoctorMapper.toResponse(doctorRepository.save(doctor));
    }
}
