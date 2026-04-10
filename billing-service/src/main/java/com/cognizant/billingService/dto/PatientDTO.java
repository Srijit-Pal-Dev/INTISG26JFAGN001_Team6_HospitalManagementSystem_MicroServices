package com.cognizant.billingService.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String bloodGroup;
    private String phoneNo;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
