package com.cognizant.pharmacyService.service;

import java.util.List;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;

public interface DispenseService {

    List<DispenseRequestResponse> getPendingRequests();

    void createDispenseRequest(CreateDispenseRequest request);

    void dispense(Long id);
}