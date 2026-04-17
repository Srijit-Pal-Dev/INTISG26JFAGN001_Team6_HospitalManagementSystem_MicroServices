package com.cognizant.pharmacyService.service;

import com.cognizant.pharmacyService.client.BillingClient.PharmacyDTO;
import java.util.List;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;

public interface DispenseService {

	List<DispenseRequestResponse> getPendingRequests();

	void createDispenseRequest(CreateDispenseRequest request);

	DispenseRequestResponse dispense(Long id);

	List<PharmacyDTO> getMedicinesByAppointmentId(Long appointmentId);
}