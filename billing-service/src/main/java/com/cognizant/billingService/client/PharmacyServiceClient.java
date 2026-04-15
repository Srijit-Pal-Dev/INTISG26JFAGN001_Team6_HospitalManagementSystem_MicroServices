package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.PharmacyDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "pharmacy-service")
public interface PharmacyServiceClient {
	@GetMapping("/medicines/appointment/{appointmentId}")
	List<PharmacyDTO> getMedicinesByAppointmentId(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable("appointmentId") Long appointmentId
	);
}
