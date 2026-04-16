package com.cognizant.pharmacyService.service.Impl;

import com.cognizant.pharmacyService.domain.Medicine;
import com.cognizant.pharmacyService.dto.MedicineRequest;
import com.cognizant.pharmacyService.dto.MedicineResponse;
import com.cognizant.pharmacyService.mapper.MedicineMapper;
import com.cognizant.pharmacyService.repository.MedicineRepository;
import com.cognizant.pharmacyService.service.MedicineService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicineServiceImpl implements MedicineService {

	@Autowired
	private MedicineRepository repository;

	@Autowired
	private MedicineMapper mapper;

	@Override
	@Transactional
	public List<MedicineResponse> getAllMedicines() {
		return repository.findAvailableMedicines().stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public List<MedicineResponse> searchMedicines(String name) {
		return repository.findByNameContainingIgnoreCase(name).stream().map(mapper::toResponse).toList();
	}

	@Override
	@Transactional
	public MedicineResponse addMedicine(MedicineRequest request) {
		return mapper.toResponse(repository.save(mapper.toEntity(request)));
	}

	@Override
	@Transactional
	public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
		Medicine medicine = repository.findById(id).orElseThrow(() -> new RuntimeException("Medicine not found"));
		medicine.setName(request.getName());
		medicine.setCategory(request.getCategory());
		medicine.setManufacturer(request.getManufacturer());
		medicine.setUnit(request.getUnit());
		medicine.setDosageStrength(request.getDosageStrength());
		medicine.setPricePerUnit(request.getPricePerUnit());
		medicine.setStockQuantity(request.getStockQuantity());
		medicine.setRequiresPrescription(request.getRequiresPrescription());
		return mapper.toResponse(repository.save(medicine));
	}

	@Override
	@Transactional
	public MedicineResponse getMedicineById(Long id) {
		Medicine medicine = repository.findById(id).orElseThrow(() -> new RuntimeException("Medicine not found"));
		return mapper.toResponse(medicine);
	}
}
