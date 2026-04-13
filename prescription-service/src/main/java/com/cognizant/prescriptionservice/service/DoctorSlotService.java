package com.cognizant.prescriptionservice.service;

import com.cognizant.prescriptionservice.dto.DoctorSlotRequest;

import java.util.List;

public interface DoctorSlotService {

    void addDoctorSlots(List<DoctorSlotRequest> slots);
}