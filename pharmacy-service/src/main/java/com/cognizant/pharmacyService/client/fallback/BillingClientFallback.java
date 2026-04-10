package com.cognizant.pharmacyService.client.fallback;

import com.cognizant.pharmacyService.client.BillingClient;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class BillingClientFallback implements BillingClient {

	@Override
	public void updateMedicineFee(Map<String, Object> request) {
		System.out.println("Billing service unavailable. Skipping medicine fee update.");
	}
}