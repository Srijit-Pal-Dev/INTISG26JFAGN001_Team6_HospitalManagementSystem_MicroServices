package com.cognizant.prescriptionservice.client;

import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback
        implements NotificationClient {

    @Override
    public void send(NotificationRequest request) {
        System.out.println(
                "Notification Service DOWN. Notification not sent."
        );
    }
}