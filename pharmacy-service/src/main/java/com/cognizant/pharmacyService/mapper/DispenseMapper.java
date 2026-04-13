package com.cognizant.pharmacyService.mapper;

import com.cognizant.pharmacyService.domain.DispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface DispenseMapper {

    @Mapping(source = "status", target = "status")
    DispenseRequestResponse toResponse(DispenseRequest dispenseRequest);
}
