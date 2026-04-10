package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.AppointmentDTO;
import com.cognizant.billingService.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "http://localhost:8081/patient")
public interface PatientServiceClient {

    @GetMapping("/patient/{patientId}")
    PatientDTO getPatientById(@PathVariable Long patientId);
    @GetMapping("/appointment/{appointmentId}")
    AppointmentDTO getAppointmentById(@PathVariable Long appointmentId);
}