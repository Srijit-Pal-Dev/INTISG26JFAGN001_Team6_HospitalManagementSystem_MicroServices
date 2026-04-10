package com.cognizant.pharmacyService.service.Impl;

import com.cognizant.pharmacyService.client.BillingClient;
import com.cognizant.pharmacyService.client.NotificationClient;
import com.cognizant.pharmacyService.domain.DispenseRequest;
import com.cognizant.pharmacyService.domain.DispenseStatus;
import com.cognizant.pharmacyService.domain.Medicine;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;
import com.cognizant.pharmacyService.repository.DispenseRequestRepository;
import com.cognizant.pharmacyService.repository.MedicineRepository;
import com.cognizant.pharmacyService.service.DispenseService;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest.MedicineItem;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DispenseServiceImpl implements DispenseService {

	private final DispenseRequestRepository dispenseRequestRepository;
	private final MedicineRepository medicineRepository;
	private final BillingClient billingClient;
	private final NotificationClient notificationClient;

	public DispenseServiceImpl(DispenseRequestRepository dispenseRequestRepository,
			MedicineRepository medicineRepository, BillingClient billingClient, NotificationClient notificationClient) {
		this.dispenseRequestRepository = dispenseRequestRepository;
		this.medicineRepository = medicineRepository;
		this.billingClient = billingClient;
		this.notificationClient = notificationClient;
	}

	@Override
	public void createDispenseRequest(CreateDispenseRequest dto) {

		for (MedicineItem item : dto.getMedicines()) {

			Medicine medicine = medicineRepository.findById(item.getMedicineId())
					.orElseThrow(() -> new RuntimeException("Medicine not found: " + item.getMedicineId()));

			if (medicine.getStockQuantity() < item.getQuantity()) {
				throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName());
			}

			BigDecimal totalPrice = medicine.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));

			DispenseRequest dispenseRequest = new DispenseRequest();

			dispenseRequest.setPrescriptionId(dto.getPrescriptionId());
			dispenseRequest.setAppointmentId(dto.getAppointmentId());
			dispenseRequest.setPatientId(dto.getPatientId());
			dispenseRequest.setMedicineId(medicine.getId());
			dispenseRequest.setMedicineName(medicine.getName());
			dispenseRequest.setQuantity(item.getQuantity());
			dispenseRequest.setUnitPrice(medicine.getPricePerUnit());
			dispenseRequest.setTotalPrice(totalPrice);
			dispenseRequest.setStatus(DispenseStatus.PENDING);

			dispenseRequestRepository.save(dispenseRequest);
		}
	}

	@Override
	public List<DispenseRequestResponse> getPendingRequests() {
		return dispenseRequestRepository.findByStatus(DispenseStatus.PENDING).stream().map(this::mapToResponse)
				.toList();
	}

	private DispenseRequestResponse mapToResponse(DispenseRequest entity) {
		DispenseRequestResponse response = new DispenseRequestResponse();
		response.setId(entity.getId());
		response.setMedicineId(entity.getMedicineId());
		response.setMedicineName(entity.getMedicineName());
		response.setQuantity(entity.getQuantity());
		response.setStatus(entity.getStatus().name());
		return response;
	}

	@Override
	@Transactional
	public void dispense(Long id) {

		DispenseRequest request = dispenseRequestRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Dispense request not found"));

		if (request.getStatus() == DispenseStatus.DISPENSED) {
			throw new RuntimeException("Medicine already dispensed");
		}

		Medicine medicine = medicineRepository.findById(request.getMedicineId())
				.orElseThrow(() -> new RuntimeException("Medicine not found"));

		if (medicine.getStockQuantity() < request.getQuantity()) {
			throw new RuntimeException("Insufficient stock for medicine");
		}

		medicine.setStockQuantity(medicine.getStockQuantity() - request.getQuantity());

		request.setStatus(DispenseStatus.DISPENSED);
		request.setDispensedAt(LocalDateTime.now());

		medicineRepository.save(medicine);
		dispenseRequestRepository.save(request);

		try {
			Map<String, Object> payload = new HashMap<>();
			payload.put("appointmentId", request.getAppointmentId());
			payload.put("medicineFee", dispenseRequestRepository.calculateTotalMedicineFee(request.getAppointmentId()));

			billingClient.updateMedicineFee(payload);
		} catch (Exception ex) {
			System.out.println("Billing service unavailable, skipping update");
		}

		try {
			Map<String, Object> notification = new HashMap<>();
			notification.put("userId", request.getPatientId());
			notification.put("title", "Medicines Ready");
			notification.put("message", "Your medicines have been dispensed");
			notification.put("type", "GENERAL");

			notificationClient.notifyUser(notification);
		} catch (Exception ex) {
			System.out.println("Notification service unavailable, skipping notification");
		}
	}
}