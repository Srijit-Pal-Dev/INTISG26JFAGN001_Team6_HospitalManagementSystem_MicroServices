package com.cognizant.patientService.client;

import com.cognizant.patientService.dto.InvoiceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service", url = "http://localhost:8086")
public interface BillingServiceClient {
	@PostMapping("/invoices/initiate")
	InvoiceDTO initiateInvoice(
		@RequestParam("patientId") Long patientId,
		@RequestParam("appointmentId") Long appointmentId
	);
}
