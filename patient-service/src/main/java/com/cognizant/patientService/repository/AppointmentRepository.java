package com.cognizant.patientService.repository;

import com.cognizant.patientService.domain.Appointment;
import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByPatientId(Long patientId);

    Optional<Appointment> findByDoctorId(Long doctorId);

    Optional<Appointment> findByStatus(Status status);
}
