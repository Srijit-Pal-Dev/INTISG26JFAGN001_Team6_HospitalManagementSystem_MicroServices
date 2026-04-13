package com.cognizant.labService.client;

import com.cognizant.labService.dto.NotificationResponse;
import com.cognizant.labService.dto.SendNotificationRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void send(@Valid @RequestBody NotificationResponse request);
}
