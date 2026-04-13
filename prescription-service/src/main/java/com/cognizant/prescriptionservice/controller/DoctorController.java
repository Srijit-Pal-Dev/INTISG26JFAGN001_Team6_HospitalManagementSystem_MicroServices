package com.cognizant.prescriptionservice.controller;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import com.cognizant.prescriptionservice.service.DoctorService;
import com.cognizant.prescriptionservice.service.DoctorSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorSlotService doctorSlotService;

    public DoctorController(
            DoctorService doctorService,
            DoctorSlotService doctorSlotService
    ){
        this.doctorService = doctorService;
        this.doctorSlotService = doctorSlotService;
    }

    /**
     * Get doctor profile
     * Role is received via request header
     */
    @GetMapping("/profile/{userId}")
    public DoctorResponse getDoctorProfile(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long userId
    ) {
        // optional validation
        if (!"DOCTOR".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: only DOCTOR role allowed");
        }
        return doctorService.getDoctorProfile(userId);
    }

    /**
     * Update doctor profile
     */
    @PutMapping("/profile/update/{userId}")
    public DoctorResponse updateDoctorProfile(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long userId,
            @Valid @RequestBody DoctorProfileRequest request
    ) {
        if (!"DOCTOR".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: only DOCTOR role allowed");
        }
        return doctorService.updateDoctorProfile(userId, request);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createSlot(
            @RequestHeader("X-User-Role") String role,
            @RequestBody DoctorSlotRequest slot
    ) {
        if (!"DOCTOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only DOCTOR can create slots");
        }

        doctorSlotService.addDoctorSlots(List.of(slot));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Doctor slot created successfully");
    }

    /**
     * ✅ CREATE MANY SLOTS
     */
    @PostMapping("/create-many")
    public ResponseEntity<String> createManySlots(
            @RequestHeader("X-User-Role") String role,
            @RequestBody List<DoctorSlotRequest> slots
    ) {
        if (!"DOCTOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only DOCTOR can create slots");
        }

        doctorSlotService.addDoctorSlots(slots);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Doctor slots created successfully");
    }

}

