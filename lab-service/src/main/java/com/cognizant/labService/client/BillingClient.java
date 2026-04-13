package com.cognizant.labService.client;

import com.cognizant.labService.dto.InvoiceUpdateResponse;
import com.cognizant.labService.dto.LabTestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name="billing-service")
public interface BillingClient {

    @PutMapping("/update/lab-fee/{appointmentId}")
    InvoiceUpdateResponse updateLabFee(
            @PathVariable Long appointmentId,
            @RequestParam BigDecimal labFee,
            @RequestBody List<LabTestResponse> labTests);
}
