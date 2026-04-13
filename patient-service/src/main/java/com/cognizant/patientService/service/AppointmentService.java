package com.cognizant.patientService.service;

import com.cognizant.patientService.domain.Status;
import com.cognizant.patientService.dto.AppointmentDTO;
import java.util.List;

public interface AppointmentService {
	AppointmentDTO scheduleAppointment(AppointmentDTO appointmentDTO);

	AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO);

	AppointmentDTO getAppointmentById(Long id);

	AppointmentDTO getAppointmentByPatientId(Long patientId);

	AppointmentDTO getAppointmentByDoctorId(Long doctorId);

	AppointmentDTO getAppointmentByStatus(Status status);

	List<AppointmentDTO> getAllAppointments();

	void deleteAppointment(Long id);

    AppointmentDTO completeAppointment(Long appointmentId);
}
