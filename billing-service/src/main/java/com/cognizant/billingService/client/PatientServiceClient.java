package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.AppointmentDTO;
import com.cognizant.billingService.dto.PatientDTO;
import com.cognizant.billingService.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {
	@GetMapping("/patient/id/{id}")
	ApiResponse<PatientDTO> getPatientById(@RequestHeader("X-User-Role") String roles, @PathVariable("id") Long id);

	@GetMapping("/appointment/id/{id}")
	ApiResponse<AppointmentDTO> getAppointmentById(@RequestHeader("X-User-Role") String roles, @PathVariable("id") Long id);
}
