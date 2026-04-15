package com.cognizant.prescriptionservice.mapper;

import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;

public class DoctorMapper {

	private DoctorMapper() {
		// utility class
	}

	public static Doctor toEntity(DoctorProfileRequest request) {
		if (request == null) {
			return null;
		}
		return Doctor
			.builder()
			.userId(request.getUserId())
			.fullName(request.getFullName())
			.specialty(request.getSpecialty())
			.qualification(request.getQualification())
			.experienceYears(request.getExperienceYears())
			.consultationFee(request.getConsultationFee())
			.build();
	}

	public static DoctorResponse toDTO(Doctor response) {
		if (response == null) {
			return null;
		}
		return DoctorResponse
			.builder()
			.id(response.getId())
			.userId(response.getUserId())
			.fullName(response.getFullName())
			.specialty(response.getSpecialty())
			.qualification(response.getQualification())
			.experienceYears(response.getExperienceYears())
			.consultationFee(response.getConsultationFee())
			.build();
	}
}
