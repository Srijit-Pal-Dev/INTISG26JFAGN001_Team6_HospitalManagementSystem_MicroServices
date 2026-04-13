package com.cognizant.labService.repository;

import com.cognizant.labService.domain.LabTest;
import com.cognizant.labService.domain.LabTestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LabTestRepository extends JpaRepository<LabTest, Long>{
    List<LabTest> findByStatus(LabTestStatus status);
    List<LabTest> findByAppointmentId(Long appointmentId);

    List<LabTest> findByPatientId(Long patientId);
}
