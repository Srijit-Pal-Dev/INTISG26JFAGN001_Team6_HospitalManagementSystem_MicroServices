package com.cognizant.prescriptionservice.client;

import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE",
        fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void send(@RequestBody NotificationRequest request);

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    class NotificationRequest {
        private Long userId;
        private String title;
        private String message;
        private String type;
    }
}