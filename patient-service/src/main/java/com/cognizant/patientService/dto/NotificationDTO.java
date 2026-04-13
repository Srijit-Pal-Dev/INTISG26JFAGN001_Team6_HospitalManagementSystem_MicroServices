package com.cognizant.patientService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

	@NotNull(message = "User Id is required")
	private Long userId;

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Message is required")
	private String message;

	@NotNull(message = "Notification type is required")
	private NotificationType type;
}
