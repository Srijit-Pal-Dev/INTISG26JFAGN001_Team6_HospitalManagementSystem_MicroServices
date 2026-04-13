package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.client.AppointmentServiceClient;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorSlotServiceImpl implements DoctorSlotService {

    private final AppointmentServiceClient appointmentServiceClient;

    @Override
    public void createSlot(DoctorSlotRequest slot) {
        appointmentServiceClient.createSlot("DOCTOR", slot);
    }

    @Override
    public void addDoctorSlots(List<DoctorSlotRequest> slots) {
        for (DoctorSlotRequest slot : slots) {
            createSlot(slot);
        }
    }
}