package com.cognizant.patientService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@Builder
@ToString
public class PatientDTO {

	private Long id;
	private Long userId;
	private String mrn;

	@NotBlank(message = "Full name cannot be blank")
	private String fullName;

	@NotNull(message = "Date of birth cannot be null")
	private LocalDate dob;

	@NotNull(message = "Age cannot be blank")
	private int age;

	@NotNull(message = "Gender can not be null")
	private String gender;

	@NotNull(message = "Blood group cannot be null")
	private String bloodGroup;

	@NotNull(message = "Phone number cannot be null")
	private String phoneNo;

	@NotNull(message = "Address cannot be null")
	private String address;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
