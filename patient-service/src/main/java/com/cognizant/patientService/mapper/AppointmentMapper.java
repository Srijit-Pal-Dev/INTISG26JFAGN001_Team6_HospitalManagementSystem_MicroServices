package com.cognizant.patientService.mapper;

import com.cognizant.patientService.domain.Appointment;
import com.cognizant.patientService.domain.Patient;
import com.cognizant.patientService.dto.AppointmentDTO;

public class AppointmentMapper {

	public static AppointmentDTO toDTO(Appointment appointment) {
		if (appointment == null) {
			return null;
		}
		return AppointmentDTO
			.builder()
			.id(appointment.getId())
			.doctorId(appointment.getDoctorId())
			.slotId(appointment.getSlotId())
			.reason(appointment.getReason())
			.status(appointment.getStatus())
			.appointmentDate(appointment.getAppointmentDate())
			.appointmentTime(appointment.getAppointmentTime())
			.createdAt(appointment.getCreatedAt())
			.updatedAt(appointment.getUpdatedAt())
			.patientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
			.build();
	}

	public static Appointment toEntity(AppointmentDTO appointmentDTO, Patient patient) {
		if (appointmentDTO == null) {
			return null;
		}
		Appointment appointment = new Appointment();
		appointment.setId(appointmentDTO.getId());
		appointment.setDoctorId(appointmentDTO.getDoctorId());
		appointment.setSlotId(appointmentDTO.getSlotId());
		appointment.setReason(appointmentDTO.getReason());
		appointment.setStatus(appointmentDTO.getStatus());
		appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
		appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
		appointment.setPatient(patient);

		return appointment;
	}
}
