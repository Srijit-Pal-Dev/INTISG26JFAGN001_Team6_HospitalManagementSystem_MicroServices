package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.client.AppointmentServiceClient;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorSlotServiceImpl implements DoctorSlotService {

    private final AppointmentServiceClient appointmentServiceClient;

    @Override
    public void addDoctorSlots(List<DoctorSlotRequest> slots) {
        appointmentServiceClient.addDoctorSlots("DOCTOR", slots);
    }
}