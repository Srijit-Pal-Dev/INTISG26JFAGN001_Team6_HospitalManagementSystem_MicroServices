package com.cognizant.userservice.client;

import com.cognizant.userservice.dto.SendNotificationRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void send(@Valid @RequestBody SendNotificationRequest request);
}
