package com.cognizant.billingService.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "medi_claim")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Data
public class Mediclaim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long patientId;
	private Long invoiceId;
	private Long paymentId;
	private String policyNumber;
	private String insurerName;
	private BigDecimal coveragePercentage;
	private BigDecimal refundAmount;

	@Enumerated(EnumType.STRING)
	private MediclaimStatus status;

	@CreationTimestamp
	private LocalDateTime appliedAt;

	@UpdateTimestamp
	private LocalDateTime processedAt;
}
