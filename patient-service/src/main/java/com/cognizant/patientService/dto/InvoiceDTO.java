package com.cognizant.patientService.dto;

import java.math.BigDecimal;

public class InvoiceDTO {

	private Long id;
	private Long patientId;
	private Long appointmentId;
	private Long doctorId;
	private BigDecimal consultationFee;
}
