package com.cognizant.patientService.service;

import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import java.util.List;

public interface AppointmentService {
    AppointmentDTO scheduleAppointment(Long userId, AppointmentDTO appointmentDTO);

    AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO);

    AppointmentDTO getAppointmentById(Long id);

    List<AppointmentDTO> getAppointmentByPatientId(Long patientId);

    List<AppointmentDTO> getAppointmentByDoctorId(Long doctorId);

    AppointmentDTO getAppointmentByStatus(Status status);

    List<AppointmentDTO> getAllAppointments();

    void deleteAppointment(Long id);

    AppointmentDTO completeAppointment(Long appointmentId);
}
