package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.client.LabClient;
import com.cognizant.prescriptionservice.client.PharmacyClient;
import com.cognizant.prescriptionservice.domain.Doctor;
import com.cognizant.prescriptionservice.domain.Prescription;
import com.cognizant.prescriptionservice.dto.CreatePrescriptionRequest;
import com.cognizant.prescriptionservice.dto.PrescriptionResponse;
import com.cognizant.prescriptionservice.exception.ResourceNotFoundException;
import com.cognizant.prescriptionservice.mapper.PrescriptionMapper;
import com.cognizant.prescriptionservice.repository.DoctorRepository;
import com.cognizant.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

	private final PrescriptionRepository prescriptionRepository;
	private final DoctorRepository doctorRepository;
    private final LabClient labClient;
    private final PharmacyClient pharmacyClient;

//	@Override
//	@Transactional
//	public PrescriptionResponse createPrescription(Long userId, CreatePrescriptionRequest request) {
//		System.out.println(">>> createPrescription called with userId=" + userId);
//
//		Doctor doctor = doctorRepository
//			.findByUserId(userId)
//			.orElseGet(() -> {
//				System.out.println(">>> Doctor not found by userId=" + userId + ", trying findById...");
//				return doctorRepository
//					.findById(userId)
//					.orElseThrow(() ->
//						new ResourceNotFoundException(
//							"Doctor profile not found for userId=" +
//							userId +
//							". Ensure the doctor has created their profile via /doctors/profile/create"
//						)
//					);
//			});
//
//		Prescription prescription = PrescriptionMapper.toEntity(request, doctor);
//
//		Prescription saved = prescriptionRepository.save(prescription);
//
//		return PrescriptionMapper.toResponse(saved);
//	}
    @Override
    @Transactional
    public PrescriptionResponse createPrescription(Long userId, CreatePrescriptionRequest request) {
        System.out.println(">>> createPrescription called with userId=" + userId);

        Doctor doctor = doctorRepository
                .findByUserId(userId)
                .orElseGet(() -> {
                    System.out.println(">>> Doctor not found by userId=" + userId + ", trying findById...");
                    return doctorRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Doctor profile not found for userId=" +
                                                    userId +
                                                    ". Ensure the doctor has created their profile via /doctors/profile/create"
                                    )
                            );
                });

        Prescription prescription = PrescriptionMapper.toEntity(request, doctor);
        Prescription saved = prescriptionRepository.save(prescription);

        // ── Trigger Lab Tests ────────────────────────────────────────────
        if (Boolean.TRUE.equals(request.getLabRequired())
                && request.getLabTests() != null
                && !request.getLabTests().isEmpty()) {

            List<LabClient.LabTestResponse> labTestItems = request.getLabTests()
                    .stream()
                    .map(t -> {
                        LabClient.LabTestResponse item = new LabClient.LabTestResponse();
                        item.setPatientId(request.getPatientId());
                        item.setAppointmentId(request.getAppointmentId());
                        item.setTestName(t.getTestName());
                        item.setFee(t.getFee());
                        return item;
                    })
                    .toList();

            LabClient.LabTestRequest labTestRequest = new LabClient.LabTestRequest();
            labTestRequest.setAppointmentId(request.getAppointmentId());
            labTestRequest.setPatientId(request.getPatientId());
            labTestRequest.setTests(labTestItems);

//            try {
//                List<LabClient.LabTestResponse> labResponses = labClient.createTest("DOCTOR", labTestRequest);
//                System.out.println(">>> Lab tests created: " + labResponses.size());
//            } catch (Exception e) {
//                System.err.println(">>> Failed to create lab tests: " + e.getMessage());
//            }
            try {
                labClient.createTest("DOCTOR", labTestRequest);
                System.out.println(">>> Lab tests created successfully");
            } catch (Exception e) {
                System.err.println(">>> Failed to create lab tests: " + e.getMessage());
            }
        }

        // ── Trigger Dispense Requests ────────────────────────────────────
        if (request.getMedicines() != null && !request.getMedicines().isEmpty()) {

            List<PharmacyClient.MedicineItem> medicineItems = request.getMedicines()
                    .stream()
                    .map(m -> {
                        PharmacyClient.MedicineItem item = new PharmacyClient.MedicineItem();
                        item.setMedicineId(m.getMedicineId());
                        item.setQuantity(m.getQuantity() != null ? m.getQuantity() : 1);
                        return item;
                    })
                    .toList();

            PharmacyClient.DispenseRequest dispenseRequest = new PharmacyClient.DispenseRequest();
            dispenseRequest.setPrescriptionId(saved.getId());
            dispenseRequest.setPatientId(request.getPatientId());
            dispenseRequest.setAppointmentId(request.getAppointmentId());
            dispenseRequest.setMedicines(medicineItems);

            try {
                pharmacyClient.createDispenseRequest("DOCTOR", dispenseRequest);
                System.out.println(">>> Dispense request created successfully");
            } catch (Exception e) {
                System.err.println(">>> Failed to create dispense request: " + e.getMessage());
            }
        }

        return PrescriptionMapper.toResponse(saved);
    }

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponse getPrescriptionById(Long id) {
		Prescription prescription = prescriptionRepository
			.findDetailedById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));

		return PrescriptionMapper.toResponse(prescription);
	}

	@Override
	@Transactional(readOnly = true)
	public PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId) {
		Prescription prescription = prescriptionRepository
			.findByAppointmentId(appointmentId)
			.orElseThrow(() -> new ResourceNotFoundException("Prescription not found for appointment"));

		return PrescriptionMapper.toResponse(prescription);
	}
}
