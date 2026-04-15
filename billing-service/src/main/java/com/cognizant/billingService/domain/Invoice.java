package com.cognizant.billingService.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String invoiceNumber;
	private Long patientId;
	private Long doctorId;
	private Long appointmentId;
	private BigDecimal consultationFee;
	private BigDecimal medicineFee;
	private BigDecimal labFee;
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	private InvoiceStatus invoiceStatus = InvoiceStatus.PENDING;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL)
	private Payment payment;

	public BigDecimal calculateTotalAmount() {
		return (
			this.totalAmount =
				(consultationFee == null ? BigDecimal.ZERO : consultationFee).add(
						medicineFee == null ? BigDecimal.ZERO : medicineFee
					)
					.add(labFee == null ? BigDecimal.ZERO : labFee)
		);
	}
}
