package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.LabDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "lab-service", url = "http://localhost:8082")
public interface LabServiceClient {
    @GetMapping("/lab/appointment/{appointmentId}/tests")
    List<LabDTO> getLabTestsByAppointmentId(@PathVariable("appointmentId") Long appointmentId);
}


