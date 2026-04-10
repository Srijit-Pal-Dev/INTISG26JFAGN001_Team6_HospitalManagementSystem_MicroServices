package com.cognizant.billingService.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorDTO {

	private Long id;
	private Long userId;

	private String fullName;
	private String specialty;
	private String qualification;
	private int experienceYears;
	private BigDecimal consultationFee;
}
