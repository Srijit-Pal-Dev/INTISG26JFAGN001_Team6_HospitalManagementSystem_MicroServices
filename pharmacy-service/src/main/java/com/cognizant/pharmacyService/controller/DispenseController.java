package com.cognizant.pharmacyService.controller;

import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;
import com.cognizant.pharmacyService.service.DispenseService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/dispense")
public class DispenseController {

    private final DispenseService dispenseService;

    public DispenseController(DispenseService dispenseService) {
        this.dispenseService = dispenseService;
    }

    @PostMapping
    public ResponseEntity<String> createDispenseRequest(
            @RequestHeader("X-User-Role") String role,
            @RequestBody CreateDispenseRequest request
    ) {
        if (!role.contains("DOCTOR") && !role.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only doctors can create dispense requests");
        }

        dispenseService.createDispenseRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Dispense request created successfully");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DispenseRequestResponse>> getPendingDispenseRequests(
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.contains("PHARMACIST") && !role.contains("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only pharmacists can view pending dispense requests"
            );
        }

        return ResponseEntity.ok(dispenseService.getPendingRequests());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispenseRequestResponse> dispenseMedicine(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id
    ) {
        if (!role.contains("PHARMACIST") && !role.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only pharmacists can dispense medicines");
        }

        DispenseRequestResponse response = dispenseService.dispense(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/prescription/{prescriptionId}")
    public ResponseEntity<List<DispenseRequestResponse>> updateDispenseRequest(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long prescriptionId,
            @RequestBody CreateDispenseRequest request
    ) {
        if (!role.contains("PHARMACIST") && !role.contains("ADMIN") &&
                !role.contains("DOCTOR")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only pharmacists or doctors can edit dispense requests");
        }

        List<DispenseRequestResponse> updated =
                dispenseService.updateDispenseRequest(prescriptionId, request);
        return ResponseEntity.ok(updated);
    }
}
