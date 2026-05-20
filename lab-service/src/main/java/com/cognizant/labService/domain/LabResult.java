package com.cognizant.labService.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String testName;
	private String resultValue;
	private String unit;
	private BigDecimal fee;
	private String referenceRange;
	private Boolean isAbnormal;

	@Column(columnDefinition = "TEXT")
	private String notes;

	private String recordedBy;

	@CreationTimestamp
	private LocalDateTime recordedAt = LocalDateTime.now();

	@OneToOne
	@JoinColumn(name = "lab_test_id", referencedColumnName = "id")
	private LabTest labTest;
}
