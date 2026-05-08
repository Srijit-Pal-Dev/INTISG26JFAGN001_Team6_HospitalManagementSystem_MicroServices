package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DoctorService {

    DoctorResponse createDoctorProfile(DoctorProfileRequest request, Long userId);

    DoctorResponse getDoctorProfile(Long userId);

    DoctorResponse updateDoctorProfile(Long userId, DoctorProfileRequest request);

    DoctorResponse getDoctorById(Long doctorId);

    @Transactional
    List<DoctorResponse> getAllDoctor();
}

