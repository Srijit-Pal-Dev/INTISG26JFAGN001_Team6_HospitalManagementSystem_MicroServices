package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.dto.CreatePrescriptionRequest;
import com.cognizant.prescriptionservice.dto.PrescriptionResponse;

public interface PrescriptionService {
	// ✅ THIS METHOD MUST EXIST
	PrescriptionResponse createPrescription(Long userId, CreatePrescriptionRequest request);

	PrescriptionResponse getPrescriptionById(Long id);

	PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId);
}
