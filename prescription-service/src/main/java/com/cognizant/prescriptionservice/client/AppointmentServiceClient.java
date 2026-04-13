package com.cognizant.prescriptionservice.client;

import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "PATIENT-APPOINTMENT-SERVICE", fallback = AppointmentServiceClientFallback.class)
public interface AppointmentServiceClient {

    @PostMapping("/doctors/slots")
    void addDoctorSlots(
            @RequestHeader("X-ROLE") String role,
            @RequestBody List<DoctorSlotRequest> slots
    );
}