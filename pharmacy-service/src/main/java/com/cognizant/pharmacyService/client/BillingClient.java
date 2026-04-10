package com.cognizant.pharmacyService.client;

import com.cognizant.pharmacyService.client.fallback.BillingClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "BILLING-SERVICE", fallback = BillingClientFallback.class)
public interface BillingClient {

	@PutMapping("/update/medicine-fee")
	void updateMedicineFee(@RequestBody Map<String, Object> request);
}