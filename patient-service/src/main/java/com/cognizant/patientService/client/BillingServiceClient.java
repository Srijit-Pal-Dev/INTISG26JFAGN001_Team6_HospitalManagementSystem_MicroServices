package com.cognizant.patientService.client;

import com.cognizant.patientService.dto.InvoiceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service")
public interface BillingServiceClient {
	@PostMapping("/invoice/generate/{patientId}/{appointmentId}")
	InvoiceDTO initiateInvoice(
		@PathVariable("patientId") Long patientId,
		@PathVariable("appointmentId") Long appointmentId
	);
}
