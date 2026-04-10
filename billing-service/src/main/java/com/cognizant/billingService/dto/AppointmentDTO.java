package com.cognizant.billingService.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Data
public class AppointmentDTO {

	private Long id;
	private Long patientId;
	private Long doctorId;
	private Long slotId;
	private String reason;
	private Status status;
	private LocalDate appointmentDate;
	private LocalTime appointmentTime;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
