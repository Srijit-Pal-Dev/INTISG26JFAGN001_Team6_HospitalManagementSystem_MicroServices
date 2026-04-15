package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;

public interface DoctorService {

    DoctorResponse createDoctorProfile(DoctorProfileRequest request, Long userId);

    DoctorResponse getDoctorProfile(Long userId);

    DoctorResponse updateDoctorProfile(Long userId, DoctorProfileRequest request);

    DoctorResponse getDoctorById(Long doctorId);
}