package com.cognizant.prescriptionservice.repository;

import com.cognizant.prescriptionservice.domain.Prescription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @EntityGraph(attributePaths = {"medicines", "labTests"})
    Optional<Prescription> findDetailedById(Long id);

    Optional<Prescription> findByAppointmentId(Long appointmentId);
}