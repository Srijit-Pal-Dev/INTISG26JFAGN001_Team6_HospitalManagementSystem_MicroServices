package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.NotificationDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationServiceClient {
	@PostMapping("/notifications/send")
	NotificationDTO send(@RequestBody NotificationDTO notificationDTO);

	@GetMapping("/notifications/{userId}/allMessages")
	List<NotificationDTO> getAll(@PathVariable Long userId);
}
