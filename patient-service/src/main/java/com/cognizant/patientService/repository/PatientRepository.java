package com.cognizant.patientService.repository;

import com.cognizant.patientService.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findPatientByMrn(String mrn);
}
