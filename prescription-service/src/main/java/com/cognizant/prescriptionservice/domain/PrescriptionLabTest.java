package com.cognizant.prescriptionservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "prescription_lab_tests")
@Getter
@Setter
public class PrescriptionLabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_code")
    private String testCode;

    @Column(name = "test_name", nullable = false)
    private String testName;

    private String notes;

    @Column
    private BigDecimal fee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;
}