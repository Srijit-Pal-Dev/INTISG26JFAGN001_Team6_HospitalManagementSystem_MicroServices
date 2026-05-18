package com.cognizant.billingService.service;

import com.cognizant.billingService.domain.MediclaimStatus;
import com.cognizant.billingService.dto.MediclaimDTO;
import java.util.List;

public interface MediclaimService {
	MediclaimDTO createMediclaim(MediclaimDTO mediclaimDTO);

	MediclaimDTO updateMediclaimStatus(Long userId, Long id, MediclaimStatus status);

	MediclaimDTO getMediclaimById(Long id);

	List<MediclaimDTO> getAllMediclaimsByPatientId(Long patientId);

	List<MediclaimDTO> getAllMediclaims();

	List<MediclaimDTO> getMediclaimsByStatus(MediclaimStatus status);
}
