package com.cognizant.pharmacyService.controller;

import java.util.List;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;
import com.cognizant.pharmacyService.service.DispenseService;
import org.springframework.http.HttpStatus;
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
    public void createDispenseRequest(
            @RequestHeader("X-User-Role") String role,
            @RequestBody CreateDispenseRequest request) {

        if (!"DOCTOR".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only doctors can create dispense requests"
            );
        }

        dispenseService.createDispenseRequest(request);
    }

    @GetMapping("/pending")
    public List<DispenseRequestResponse> getPendingDispenseRequests(
            @RequestHeader("X-User-Role") String role) {

        if (!"PHARMACIST".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only pharmacists can view pending dispense requests"
            );
        }

        return dispenseService.getPendingRequests();
    }

    @PutMapping("/{id}")
    public void dispenseMedicine(
            @RequestHeader("X-User-Role") String role,
            @PathVariable Long id) {

        if (!"PHARMACIST".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only pharmacists can dispense medicines"
            );
        }

        dispenseService.dispense(id);
    }
}