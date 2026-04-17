package com.cognizant.billingService.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LabDTO {

	private Long id;
	private Long patientId;
	private Long appointmentId;
	private String testName;
	private String testCode;
	private String status;
	private BigDecimal fee;
	private LocalDateTime createdAt;
}
