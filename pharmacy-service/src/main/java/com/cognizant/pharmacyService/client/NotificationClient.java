package com.cognizant.pharmacyService.client;

import com.cognizant.pharmacyService.client.fallback.NotificationClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "NOTIFICATION-SERVICE", fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping("/send")
    void notifyUser(@RequestBody Map<String, Object> request);
}

