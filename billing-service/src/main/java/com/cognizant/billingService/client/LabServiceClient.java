package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.LabDTO;
import java.util.List;

import com.cognizant.billingService.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "lab-service")
public interface LabServiceClient {
	@GetMapping("/lab-tests/appointment/tests/{appointmentId}")
	ApiResponse<List<LabDTO>> getLabTestsByAppointmentId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable("appointmentId") Long appointmentId
	);
}
