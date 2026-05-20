//package com.cognizant.prescriptionservice.dto;
//
//import jakarta.validation.constraints.NotBlank;
//import lombok.*;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PrescriptionLabTestRequest {
//
//	@NotBlank
//	private String testName;
//
//	private String notes;
//}
package com.cognizant.prescriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionLabTestRequest {

    @NotBlank
    private String testName;

    private String notes;

    private BigDecimal fee;
}