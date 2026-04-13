package com.cognizant.prescriptionservice.controller;

import com.cognizant.prescriptionservice.dto.DoctorProfileRequest;
import com.cognizant.prescriptionservice.dto.DoctorResponse;
import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;
import com.cognizant.prescriptionservice.service.DoctorService;
import com.cognizant.prescriptionservice.service.DoctorSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorSlotService doctorSlotService;

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

    @PostMapping("/slots")
    public ResponseEntity<String> addDoctorSlots(
            @RequestHeader("X-User-Role") String role,
            @RequestBody List<DoctorSlotRequest> slots
    ) {
        if (!"DOCTOR".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only doctors can add slots");
        }

        doctorSlotService.addDoctorSlots(slots);
        return ResponseEntity.ok("Doctor slots added successfully");
    }
}

