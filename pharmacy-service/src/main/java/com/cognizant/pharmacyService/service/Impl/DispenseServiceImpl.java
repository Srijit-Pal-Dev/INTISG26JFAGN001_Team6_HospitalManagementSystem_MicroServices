package com.cognizant.pharmacyService.service.Impl;

import com.cognizant.pharmacyService.client.BillingClient;
import com.cognizant.pharmacyService.client.BillingClient.PharmacyDTO;
import com.cognizant.pharmacyService.client.NotificationClient;
import com.cognizant.pharmacyService.client.NotificationClient.SendNotificationRequest;
import com.cognizant.pharmacyService.domain.DispenseRequest;
import com.cognizant.pharmacyService.domain.DispenseStatus;
import com.cognizant.pharmacyService.domain.Medicine;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest;
import com.cognizant.pharmacyService.dto.CreateDispenseRequest.MedicineItem;
import com.cognizant.pharmacyService.dto.DispenseRequestResponse;
import com.cognizant.pharmacyService.repository.DispenseRequestRepository;
import com.cognizant.pharmacyService.repository.MedicineRepository;
import com.cognizant.pharmacyService.service.DispenseService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispenseServiceImpl implements DispenseService {

	private final DispenseRequestRepository dispenseRequestRepository;
	private final MedicineRepository medicineRepository;
	private final BillingClient billingClient;
	private final NotificationClient notificationClient;

	public DispenseServiceImpl(
		DispenseRequestRepository dispenseRequestRepository,
		MedicineRepository medicineRepository,
		BillingClient billingClient,
		NotificationClient notificationClient
	) {
		this.dispenseRequestRepository = dispenseRequestRepository;
		this.medicineRepository = medicineRepository;
		this.billingClient = billingClient;
		this.notificationClient = notificationClient;
	}

	@Override
	@Transactional
	public void createDispenseRequest(CreateDispenseRequest dto) {
		for (MedicineItem item : dto.getMedicines()) {
			Medicine medicine = medicineRepository
				.findById(item.getMedicineId())
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
            dispenseRequest.setDispensedAt(LocalDateTime.now());
            System.out.println(">>> Creating dispense request: " + dispenseRequest);
			dispenseRequestRepository.save(dispenseRequest);
		}
	}

	@Override
	@Transactional
	public List<DispenseRequestResponse> getPendingRequests() {
		return dispenseRequestRepository
			.findByStatus(DispenseStatus.PENDING)
			.stream()
			.map(this::mapToResponse)
			.collect(Collectors.toList());
	}

	private DispenseRequestResponse mapToResponse(DispenseRequest entity) {
		DispenseRequestResponse response = new DispenseRequestResponse();
		response.setId(entity.getId());
		response.setPrescriptionId(entity.getPrescriptionId());
		response.setPatientId(entity.getPatientId());
		response.setAppointmentId(entity.getAppointmentId());
		response.setMedicineId(entity.getMedicineId());
		response.setMedicineName(entity.getMedicineName());
		response.setQuantity(entity.getQuantity());
		response.setUnitPrice(entity.getUnitPrice());
		response.setTotalPrice(entity.getTotalPrice());
		response.setStatus(entity.getStatus().name());
		response.setDispensedAt(entity.getDispensedAt());
		response.setCreatedAt(entity.getCreatedAt());
		return response;
	}

	@Override
	@Transactional
	public DispenseRequestResponse dispense(Long id) {
		DispenseRequest request = dispenseRequestRepository
			.findById(id)
			.orElseThrow(() -> new RuntimeException("Dispense request not found"));

		if (request.getStatus() == DispenseStatus.DISPENSED) {
			throw new RuntimeException("Medicine already dispensed");
		}

		Medicine medicine = medicineRepository
			.findById(request.getMedicineId())
			.orElseThrow(() -> new RuntimeException("Medicine not found"));

		if (medicine.getStockQuantity() < request.getQuantity()) {
			throw new RuntimeException("Insufficient stock for medicine");
		}

		medicine.setStockQuantity(medicine.getStockQuantity() - request.getQuantity());

		request.setStatus(DispenseStatus.DISPENSED);
		request.setDispensedAt(LocalDateTime.now());

		medicineRepository.save(medicine);
		dispenseRequestRepository.save(request);

		// Compute cumulative medicine fee for entire appointment (same pattern as lab-service)
		Long appointmentId = request.getAppointmentId();
		List<PharmacyDTO> allDispensedMedicines = getMedicinesByAppointmentId(appointmentId);
		BigDecimal totalMedicineFee = dispenseRequestRepository.calculateTotalMedicineFee(appointmentId);
		if (totalMedicineFee == null) {
			totalMedicineFee = BigDecimal.ZERO;
		}

		System.out.println(
			">>> Calling billing: appointmentId=" +
			appointmentId +
			", totalMedicineFee=" +
			totalMedicineFee +
			", medicineCount=" +
			allDispensedMedicines.size()
		);

		// Call billing service directly (no try-catch) — matches lab-service pattern
		billingClient.updateMedicineFee("PHARMACIST", appointmentId, totalMedicineFee, allDispensedMedicines);

		try {
			SendNotificationRequest notification = new SendNotificationRequest();
			notification.setUserId(request.getPatientId());
			notification.setTitle("Medicines Ready");
			notification.setMessage("Your medicines have been dispensed");
			notification.setType(NotificationClient.NotificationType.GENERAL);
			notificationClient.send(notification);
		} catch (Exception ex) {
			System.out.println("Notification service unavailable, skipping notification");
		}

		DispenseRequestResponse response = new DispenseRequestResponse();
		response.setId(request.getId());
		response.setPrescriptionId(request.getPrescriptionId());
		response.setPatientId(request.getPatientId());
		response.setAppointmentId(request.getAppointmentId());
		response.setMedicineId(request.getMedicineId());
		response.setMedicineName(request.getMedicineName());
		response.setQuantity(request.getQuantity());
		response.setUnitPrice(request.getUnitPrice());
		response.setTotalPrice(request.getTotalPrice());
		response.setStatus(request.getStatus().name());
		response.setDispensedAt(request.getDispensedAt());
		response.setCreatedAt(request.getCreatedAt());
		return response;
	}

	@Override
	@Transactional
	public List<PharmacyDTO> getMedicinesByAppointmentId(Long appointmentId) {
		return dispenseRequestRepository
			.findByAppointmentIdAndStatus(appointmentId, DispenseStatus.DISPENSED)
			.stream()
			.map(request -> {
				PharmacyDTO dto = new PharmacyDTO();
				dto.setMedicineId(request.getMedicineId());
				dto.setMedicineName(request.getMedicineName());
				dto.setQuantity(request.getQuantity());
				dto.setUnitPrice(request.getUnitPrice());
				dto.setTotalPrice(request.getTotalPrice());
				return dto;
			})
			.toList();
	}
}
