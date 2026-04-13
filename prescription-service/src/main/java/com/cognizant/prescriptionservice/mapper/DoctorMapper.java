package com.cognizant.prescriptionservice.mapper;

import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;

public class DoctorMapper {

    private DoctorMapper() {
        // utility class
    }

    public static DoctorResponse toResponse(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUserId())
                .fullName(doctor.getFullName())
                .specialty(doctor.getSpecialty())
                .qualification(doctor.getQualification())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .build();
    }

    public static void updateDoctorFromRequest(
            DoctorProfileRequest request,
            Doctor doctor
    ) {
        doctor.setFullName(request.getFullName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setQualification(request.getQualification());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
    }
}