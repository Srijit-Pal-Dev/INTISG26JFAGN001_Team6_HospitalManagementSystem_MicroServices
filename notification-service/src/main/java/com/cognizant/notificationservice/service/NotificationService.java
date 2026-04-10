package com.cognizant.notificationservice.service;

import com.cognizant.notificationservice.dto.NotificationResponse;
import com.cognizant.notificationservice.dto.SendNotificationRequest;

import java.util.List;

public interface NotificationService {

    NotificationResponse send(SendNotificationRequest request);

    List<NotificationResponse> getAll(Long userId);

    List<NotificationResponse> getUnread(Long userId);

    NotificationResponse markAsRead(Long id);

    void markAllAsRead(Long userId);

}
