package com.cognizant.patientService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DoctorSlotDTO {

	private Long id;
	private Long doctorId;
	private Long userId;

	@NotNull(message = "Slot date cannot be null")
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate slotDate;

	@NotNull(message = "Slot time cannot be null")
	@JsonFormat(pattern = "HH:mm")
	private LocalTime slotTime;

	private boolean booked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
