package com.cognizant.prescriptionservice.client;

import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppointmentServiceClientFallback
        implements AppointmentServiceClient {

    @Override
    public void addDoctorSlots(String role, List<DoctorSlotRequest> slots) {
        // ✅ fallback behavior
        System.out.println(
                "Appointment Service DOWN. Slots not created. Will retry later."
        );
    }
}