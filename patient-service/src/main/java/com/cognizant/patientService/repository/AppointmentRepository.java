package com.cognizant.patientService.repository;

import com.cognizant.patientService.domain.Appointment;
import com.cognizant.patientService.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    Optional<Appointment> findByStatus(Status status);
}
