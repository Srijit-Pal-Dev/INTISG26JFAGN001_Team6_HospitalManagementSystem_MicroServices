package com.cognizant.pharmacyService.client.fallback;

import com.cognizant.pharmacyService.client.NotificationClient;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificationClientFallback implements NotificationClient {

	@Override
	public void notifyUser(Map<String, Object> request) {
		System.out.println("Notification service unavailable. Skipping notification.");
	}
}