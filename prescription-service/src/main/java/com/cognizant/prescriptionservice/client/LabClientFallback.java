package com.cognizant.prescriptionservice.client;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LabClientFallback implements LabClient {

    @Override
    public List<LabTestResponse> createTest(String roles, LabTestRequest request) {
        System.out.println(
                "Lab Service DOWN. Lab test creation skipped."
        );
        return List.of();
    }
}