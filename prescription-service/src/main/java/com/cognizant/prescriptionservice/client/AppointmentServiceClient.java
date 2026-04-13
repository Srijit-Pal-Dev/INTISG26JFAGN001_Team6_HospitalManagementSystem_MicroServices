package com.cognizant.prescriptionservice.client;

import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@FeignClient(
        name = "PATIENT-APPOINTMENT-SERVICE",
        fallback = AppointmentServiceClientFallback.class
)
public interface AppointmentServiceClient {

    /* ✅ CREATE SINGLE SLOT */
    @PostMapping("/doctors/slots/create")
    DoctorSlotRequest createSlot(
            @RequestHeader("X-USER-Roles") String role,
            @RequestBody DoctorSlotRequest slot
    );

    /* ✅ CREATE MANY SLOTS */
    @PostMapping("/doctors/slots/create-many")
    List<DoctorSlotRequest> createManySlots(
            @RequestHeader("X-USER-Roles") String role,
            @RequestParam Long doctorId,
            @RequestParam
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate slotDate,
            @RequestParam
            @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam int numberOfSlots,
            @RequestParam int slotMinutes
    );
}