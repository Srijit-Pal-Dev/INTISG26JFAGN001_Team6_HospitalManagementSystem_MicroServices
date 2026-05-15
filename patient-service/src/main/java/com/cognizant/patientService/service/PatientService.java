package com.cognizant.patientService.service;

import com.cognizant.patientService.dto.PatientDTO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface PatientService {
	PatientDTO createPatient(PatientDTO patientDTO);

	PatientDTO updatePatient(Long id, PatientDTO patientDTO);

	PatientDTO getPatientById(Long id);

	PatientDTO getPatientByMrn(String mrn);

	List<PatientDTO> getAllPatient();

	@Transactional
	List<PatientDTO> getPatientByUserId(Long userId);

	void deletePatient(Long id);
}
