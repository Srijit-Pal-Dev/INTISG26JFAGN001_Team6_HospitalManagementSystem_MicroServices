package com.cognizant.pharmacyService.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateDispenseRequest {

	@NotBlank(message = "Prescription Id is required")
	private Long prescriptionId;

	@NotBlank(message = "Patient Id is required")
	private Long patientId;

	@NotBlank(message = "Patient Id is required")
	private Long appointmentId;

	@NotBlank(message = "Provide the list of medicines to be dispensed")
	private List<MedicineItem> medicines;

	public Long getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(Long prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public List<MedicineItem> getMedicines() {
		return medicines;
	}

	public void setMedicines(List<MedicineItem> medicines) {
		this.medicines = medicines;
	}

	@Getter
	@Setter
	@Builder
	public static class MedicineItem {

		@NotBlank(message = "Medicine Id is required")
		private Long medicineId;

		@NotBlank(message = "Quantity is required")
		@Min(value = 1, message = "Quantity must be greater than 0")
		private Integer quantity;

		public Long getMedicineId() {
			return medicineId;
		}

		public void setMedicineId(Long medicineId) {
			this.medicineId = medicineId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}
}