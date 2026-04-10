package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:8082/api/notifications")
public interface NotificationServiceClient {
	@PostMapping("/send")
	NotificationDTO send(@RequestBody NotificationDTO notificationDTO);
}
