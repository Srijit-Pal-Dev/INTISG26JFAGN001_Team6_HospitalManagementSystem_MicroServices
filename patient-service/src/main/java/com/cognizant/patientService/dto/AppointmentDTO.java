package com.cognizant.patientService.dto;

import com.cognizant.patientService.domain.Status;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
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

	@NotNull(message = "Reason for appointment cannot be null")
	private String reason;

	private Status status;

	@NotNull(message = "Appointment date cannot be null")
	@NotNull(message = "Appointment time cannot be null")
	private LocalDate appointmentDate;

	@NotNull(message = "Appointment time cannot be null")
	private LocalTime appointmentTime;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
