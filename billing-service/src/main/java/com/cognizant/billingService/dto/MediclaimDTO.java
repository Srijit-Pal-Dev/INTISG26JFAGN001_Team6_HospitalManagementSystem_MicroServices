package com.cognizant.billingService.dto;

import com.cognizant.billingService.domain.MediclaimStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Data
public class MediclaimDTO {

	private Long id;
	private Long patientId;
	private Long invoiceId;
	private Long paymentId;

	@NotBlank(message = "Policy number is required")
	private String policyNumber;

	@NotBlank(message = "Insurer name is required")
	private String insurerName;

	@NotNull(message = "Claim amount is required")
	private BigDecimal coveragePercentage;

	private BigDecimal refundAmount;
	private MediclaimStatus status;
	private LocalDateTime appliedAt;
	private LocalDateTime processedAt;
}
