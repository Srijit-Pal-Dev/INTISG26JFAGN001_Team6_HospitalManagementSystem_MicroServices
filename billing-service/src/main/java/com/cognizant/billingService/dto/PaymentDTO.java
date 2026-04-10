package com.cognizant.billingService.dto;

import com.cognizant.billingService.domain.PaymentMethod;
import com.cognizant.billingService.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Data
public class PaymentDTO {

	private Long id;
	private Long appointmentId;
	private Long patientId;
	private BigDecimal amount;
	private PaymentMethod paymentMethod;
	private String transactionId;
	private PaymentStatus paymentStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private InvoiceDTO invoice;
}
