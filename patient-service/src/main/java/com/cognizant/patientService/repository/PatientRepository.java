package com.cognizant.patientService.repository;

import com.cognizant.patientService.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	Optional<Patient> findPatientByMrn(String mrn);
	List<Patient> getPatientByUserId(Long userId);
}
