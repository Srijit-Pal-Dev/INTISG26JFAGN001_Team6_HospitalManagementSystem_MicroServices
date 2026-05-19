package com.cognizant.patientService.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "patient")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Patient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, unique = true)
	private String mrn;

	@Column(nullable = false, length = 50)
	private String fullName;

	@Column(nullable = false)
	private LocalDate dob;

	@Column(nullable = false)
	private int age;

	@Column(nullable = false)
	private String gender;

	@Column(nullable = false)
	private String bloodGroup;

	@Column(nullable = false, length = 20)
	private String phoneNo;

	@Column(nullable = false, length = 50)
	private String address;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
	private List<Appointment> appointments = new ArrayList<>();
}
