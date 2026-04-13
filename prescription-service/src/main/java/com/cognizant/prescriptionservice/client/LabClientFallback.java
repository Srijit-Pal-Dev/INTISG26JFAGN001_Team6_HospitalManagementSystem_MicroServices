package com.cognizant.prescriptionservice.client;

import org.springframework.stereotype.Component;

@Component
public class LabClientFallback implements LabClient {

    @Override
    public void createLabTestRequest(LabTestRequest request) {
        System.out.println(
                "Lab Service DOWN. Lab test creation skipped."
        );
    }
}