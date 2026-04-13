package com.cognizant.pharmacyService.mapper;

import com.cognizant.pharmacyService.domain.Medicine;
import com.cognizant.pharmacyService.dto.MedicineRequest;
import com.cognizant.pharmacyService.dto.MedicineResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface MedicineMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	Medicine toEntity(MedicineRequest request);
	MedicineResponse toResponse(Medicine medicine);
}