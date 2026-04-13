package com.cognizant.prescriptionservice.mapper;

import com.cognizant.prescriptionservice.domain.*;
import com.cognizant.prescriptionservice.dto.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class PrescriptionMapper {

    private PrescriptionMapper() {
    }

    // ✅ FIX IS HERE: convert List → Set
    public static Prescription toEntity(
            CreatePrescriptionRequest request,
            Doctor doctor
    ) {
        Prescription prescription = Prescription.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .diagnosis(request.getDiagnosis())
                .doctorNotes(request.getDoctorNotes())
                .labRequired(request.getLabRequired())
                .doctor(doctor)
                .createdAt(LocalDateTime.now())
                .build();

        // ✅ List → Set conversion (IMPORTANT)
        if (request.getMedicines() != null) {
            Set<PrescriptionMedicine> medicines =
                    request.getMedicines().stream()
                            .map(m -> {
                                PrescriptionMedicine pm = new PrescriptionMedicine();
                                pm.setMedicineId(m.getMedicineId());
                                pm.setMedicineName(m.getMedicineName());
                                pm.setDosage(m.getDosage());
                                pm.setFrequency(m.getFrequency());
                                pm.setDuration(m.getDuration());
                                pm.setInstructions(m.getInstructions());
                                pm.setPrescription(prescription);
                                return pm;
                            })
                            .collect(Collectors.toSet());

            prescription.setMedicines(medicines);
        }

        // ✅ List → Set conversion (IMPORTANT)
        if (request.getLabTests() != null) {
            Set<PrescriptionLabTest> labTests =
                    request.getLabTests().stream()
                            .map(t -> {
                                PrescriptionLabTest plt = new PrescriptionLabTest();
                                plt.setTestCode(t.getTestCode());
                                plt.setTestName(t.getTestName());
                                plt.setNotes(t.getNotes());
                                plt.setPrescription(prescription);
                                return plt;
                            })
                            .collect(Collectors.toSet());

            prescription.setLabTests(labTests);
        }

        return prescription;
    }

    // ✅ Entity → DTO (Set → List is fine)
    public static PrescriptionResponse toResponse(Prescription prescription) {

        PrescriptionResponse response = new PrescriptionResponse();
        response.setId(prescription.getId());
        response.setAppointmentId(prescription.getAppointmentId());
        response.setPatientId(prescription.getPatientId());
        response.setDoctorId(prescription.getDoctor().getId());
        response.setDoctorName(prescription.getDoctor().getFullName());
        response.setDiagnosis(prescription.getDiagnosis());
        response.setDoctorNotes(prescription.getDoctorNotes());
        response.setLabRequired(prescription.getLabRequired());
        response.setCreatedAt(prescription.getCreatedAt());

        response.setMedicines(
                prescription.getMedicines().stream()
                        .map(m -> new PrescriptionMedicineRequest(
                                m.getMedicineId(),
                                m.getMedicineName(),
                                m.getDosage(),
                                m.getFrequency(),
                                m.getDuration(),
                                m.getInstructions()
                        ))
                        .toList()
        );

        response.setLabTests(
                prescription.getLabTests().stream()
                        .map(l -> new PrescriptionLabTestRequest(
                                l.getTestCode(),
                                l.getTestName(),
                                l.getNotes()
                        ))
                        .toList()
        );

        return response;
    }
}