package com.cognizant.patientService.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "doctor_slot")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DoctorSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	@Column(nullable = false)
	private Long doctorId;

	@Column(nullable = false)
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate slotDate;

	@Column(nullable = false)
	@JsonFormat(pattern = "HH:mm")
	private LocalTime slotTime;

	@Column(nullable = false)
	private boolean booked;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
