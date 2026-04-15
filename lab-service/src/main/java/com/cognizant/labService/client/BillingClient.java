package com.cognizant.labService.client;

import com.cognizant.labService.dto.InvoiceUpdateResponse;
import com.cognizant.labService.dto.LabTestResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "billing-service")
public interface BillingClient {
	@PutMapping("/invoice/update/lab-fee/{appointmentId}")
	InvoiceUpdateResponse updateLabFee(
		@RequestHeader("X-User-Role") String roles,
		@PathVariable("appointmentId") Long appointmentId,
		@RequestParam BigDecimal labFee,
		@RequestBody List<LabTestResponse> labTests
	);
}
