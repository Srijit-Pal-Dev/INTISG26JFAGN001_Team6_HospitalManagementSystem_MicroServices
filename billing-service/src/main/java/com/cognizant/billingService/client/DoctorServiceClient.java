package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "prescription-service")
public interface DoctorServiceClient {
	@GetMapping("/doctors/check/{doctorId}")
	DoctorDTO getDoctorById(@RequestHeader("X-User-Role") String role, @PathVariable("doctorId") Long doctorId);
}
