package com.cognizant.prescriptionservice.client;

import org.springframework.stereotype.Component;

@Component
public class PharmacyClientFallback implements PharmacyClient {

    @Override
    public void createDispenseRequest(DispenseRequest request) {
        System.out.println(
                "Pharmacy Service DOWN. Dispense request skipped."
        );
    }
}