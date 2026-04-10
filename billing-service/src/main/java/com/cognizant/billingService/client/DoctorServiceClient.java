package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service", url = "http://localhost:8082")
public interface DoctorServiceClient {
    @GetMapping("/doctor/{doctorId}")
    DoctorDTO getDoctorById(@PathVariable Long doctorId);
}
