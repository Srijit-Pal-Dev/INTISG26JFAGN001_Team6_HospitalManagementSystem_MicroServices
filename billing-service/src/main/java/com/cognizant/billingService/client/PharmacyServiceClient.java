package com.cognizant.billingService.client;

import com.cognizant.billingService.dto.PharmacyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pharmacy-service", url = "http://localhost:8084")
public interface PharmacyServiceClient {
    @GetMapping("/pharmacy/appointment/medicines/{appointmentId}")
    List<PharmacyDTO> getMedicinesByAppointmentId(@PathVariable("appointmentId") Long appointmentId);
}
