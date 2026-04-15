package com.cognizant.billingService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate appointmentDate;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime appointmentTime;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
