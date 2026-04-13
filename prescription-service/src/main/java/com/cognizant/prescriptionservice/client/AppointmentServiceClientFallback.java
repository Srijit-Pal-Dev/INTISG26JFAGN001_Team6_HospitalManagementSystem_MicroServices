package com.cognizant.prescriptionservice.client;

import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Component
public class AppointmentServiceClientFallback
        implements AppointmentServiceClient {

    @Override
    public DoctorSlotRequest createSlot(String role, DoctorSlotRequest slot) {
        System.err.println("Appointment service DOWN – single slot not created");
        return null;
    }

    @Override
    public List<DoctorSlotRequest> createManySlots(
            String role,
            Long doctorId,
            LocalDate slotDate,
            LocalTime startTime,
            int numberOfSlots,
            int slotMinutes
    ) {
        System.err.println("Appointment service DOWN – bulk slots not created");
        return Collections.emptyList();
    }
}