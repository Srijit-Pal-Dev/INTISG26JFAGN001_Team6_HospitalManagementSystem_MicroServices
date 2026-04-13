package com.cognizant.prescriptionservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class DoctorSlotRequest {

    private Long id;
    private Long doctorId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate slotDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime slotTime;

    private boolean booked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}