package com.cognizant.prescriptionservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorSlotRequest {

	private Long id;
	private Long doctorId;
	private Long userId;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate slotDate;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime slotTime;

	private boolean booked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
