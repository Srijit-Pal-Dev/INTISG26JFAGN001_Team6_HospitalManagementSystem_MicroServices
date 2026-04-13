package com.cognizant.pharmacyService.client;

import org.springframework.http.HttpStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.Instant;

@FeignClient(name = "NOTIFICATION-SERVICE", fallback = NotificationClient.NotificationClientFallback.class)
public interface NotificationClient {

	@PostMapping("/send")
	ResponseEntity<NotificationResponse> send(@RequestBody SendNotificationRequest request);

	@Component
	class NotificationClientFallback implements NotificationClient {

		@Override
		public ResponseEntity<NotificationResponse> send(SendNotificationRequest request) {
			System.out.println(
					"Notification service unavailable. Skipping notification for userId=" + request.getUserId());

			NotificationResponse response = new NotificationResponse();
			response.setUserId(request.getUserId());
			response.setTitle(request.getTitle());
			response.setMessage(request.getMessage());
			response.setType(request.getType());
			response.setRead(false);
			response.setCreatedAt(Instant.now());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
	}

	class SendNotificationRequest {
		private Long userId;
		private String title;
		private String message;
		private NotificationType type;

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public NotificationType getType() {
			return type;
		}

		public void setType(NotificationType type) {
			this.type = type;
		}
	}

	class NotificationResponse {
		private Long id;
		private Long userId;
		private String title;
		private String message;
		private NotificationType type;
		private boolean isRead;
		private Instant createdAt;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public NotificationType getType() {
			return type;
		}

		public void setType(NotificationType type) {
			this.type = type;
		}

		public boolean isRead() {
			return isRead;
		}

		public void setRead(boolean read) {
			isRead = read;
		}

		public Instant getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Instant createdAt) {
			this.createdAt = createdAt;
		}
	}

	enum NotificationType {
		APPOINTMENT, PRESCRIPTION, LAB, BILLING, MEDICLAIM, GENERAL
	}
}