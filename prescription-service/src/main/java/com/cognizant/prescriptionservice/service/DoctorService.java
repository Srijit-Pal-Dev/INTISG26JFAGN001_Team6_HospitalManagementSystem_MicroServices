package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;

public interface DoctorService {

    DoctorResponse getDoctorProfile(Long userId);

    DoctorResponse updateDoctorProfile(Long userId, DoctorProfileRequest request);
}