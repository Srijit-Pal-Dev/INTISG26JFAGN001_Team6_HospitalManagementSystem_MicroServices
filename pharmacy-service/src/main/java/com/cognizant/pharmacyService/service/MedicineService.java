package com.cognizant.pharmacyService.service;

import com.cognizant.pharmacyService.dto.MedicineRequest;
import com.cognizant.pharmacyService.dto.MedicineResponse;

import java.util.List;

public interface MedicineService {
	List<MedicineResponse> getAllMedicines();

	List<MedicineResponse> searchMedicines(String name);

	MedicineResponse addMedicine(MedicineRequest request);

	MedicineResponse updateMedicine(Long id, MedicineRequest request);

	MedicineResponse getMedicineById(Long id);
}