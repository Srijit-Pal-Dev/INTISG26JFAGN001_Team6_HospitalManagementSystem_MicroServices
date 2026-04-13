package com.cognizant.patientService.client;

import com.cognizant.patientService.dto.NotificationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {

	@PostMapping("/notifications/send")
	NotificationDTO send(@RequestBody NotificationDTO notificationDTO);
}
