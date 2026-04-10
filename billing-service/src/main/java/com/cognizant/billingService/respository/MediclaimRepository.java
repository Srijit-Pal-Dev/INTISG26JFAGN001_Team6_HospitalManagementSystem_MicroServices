package com.cognizant.billingService.respository;

import com.cognizant.billingService.domain.Mediclaim;
import com.cognizant.billingService.domain.MediclaimStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediclaimRepository extends JpaRepository<Mediclaim, Long> {
	List<Mediclaim> findByPatientId(Long patientId);

	List<Mediclaim> findByStatus(MediclaimStatus status);
}
