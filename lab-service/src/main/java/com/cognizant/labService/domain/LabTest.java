package com.cognizant.labService.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long prescriptionId;
    private Long appointmentId;
    private Long patientId;

    private String testName;
    private String testCode;

    private String assignedTo; //Technician username

    @Enumerated(EnumType.STRING)
    @Default
    private LabTestStatus status = LabTestStatus.PENDING;

    private BigDecimal fee;

    @CreationTimestamp
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    @Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "labTest", cascade = CascadeType.ALL)
    private LabResult result;

}
