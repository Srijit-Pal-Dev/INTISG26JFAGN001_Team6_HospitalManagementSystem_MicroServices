package com.cognizant.patientService.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "appointments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Appointment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Doctor ID cannot be null")
	private Long doctorId;

	@NotNull(message = "Slot ID cannot be null")
	private Long slotId;

	private String reason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.SCHEDULED;

	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate appointmentDate;

	@JsonFormat(pattern = "HH:mm")
	private LocalTime appointmentTime;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "patientId", nullable = false)
	private Patient patient;
}
