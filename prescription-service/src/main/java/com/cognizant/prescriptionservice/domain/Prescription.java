package com.cognizant.prescriptionservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)   // ✅ REQUIRED FOR HIBERNATE
@AllArgsConstructor                               // ✅ REQUIRED FOR @Builder
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "patient_id")
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private String diagnosis;

    @Column(name = "doctor_notes")
    private String doctorNotes;

    @Column(name = "lab_required")
    private Boolean labRequired;

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PrescriptionMedicine> medicines = new HashSet<>();

    @OneToMany(
            mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PrescriptionLabTest> labTests = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}