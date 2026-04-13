package com.cognizant.patientService.repository;

import com.cognizant.patientService.domain.DoctorSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorSlotRepository extends JpaRepository<DoctorSlot, Long> {
    List<DoctorSlot> findByDoctorId(Long doctorId);
    Optional<DoctorSlot> findByDoctorIdAndSlotDateAndSlotTime(Long doctorId, LocalDate slotDate, LocalTime slotTime);
}
