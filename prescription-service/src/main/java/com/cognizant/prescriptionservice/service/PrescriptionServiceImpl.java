package com.cognizant.prescriptionservice.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorRepository doctorRepository;

    // ✅ RESTORED METHOD — THIS FIXES THE ERROR
    @Override
    public PrescriptionResponse createPrescription(
            Long doctorUserId,
            CreatePrescriptionRequest request
    ) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found"));

        Prescription prescription =
                PrescriptionMapper.toEntity(request, doctor);

        Prescription saved = prescriptionRepository.save(prescription);

        return PrescriptionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {

        Prescription prescription = prescriptionRepository.findDetailedById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Prescription not found"));

        return PrescriptionMapper.toResponse(prescription);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId) {

        Prescription prescription = prescriptionRepository
                .findByAppointmentId(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Prescription not found for appointment"));

        return PrescriptionMapper.toResponse(prescription);
    }
}