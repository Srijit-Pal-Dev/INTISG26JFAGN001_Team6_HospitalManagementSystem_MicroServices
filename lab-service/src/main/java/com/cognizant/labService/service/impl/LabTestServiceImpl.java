package com.cognizant.labService.service.impl;

import com.cognizant.labService.client.BillingClient;
import com.cognizant.labService.client.NotificationClient;
import com.cognizant.labService.domain.LabResult;
import com.cognizant.labService.domain.LabTest;
import com.cognizant.labService.domain.LabTestStatus;
import com.cognizant.labService.domain.NotificationType;
import com.cognizant.labService.dto.*;
import com.cognizant.labService.exception.LabTestNotFoundException;
import com.cognizant.labService.mapper.LabTestMapper;
import com.cognizant.labService.repository.LabResultRepository;
import com.cognizant.labService.repository.LabTestRepository;
import com.cognizant.labService.service.LabTestService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LabTestServiceImpl implements LabTestService {

	private final LabTestRepository labTestRepository;
	private final LabResultRepository labResultRepository;
	private final LabTestMapper mapper;
	private final BillingClient billingClient;
	private final NotificationClient notificationClient;

	public LabTestServiceImpl(
		LabTestRepository labTestRepository,
		LabResultRepository labResultRepository,
		LabTestMapper mapper,
		BillingClient billingClient,
		NotificationClient notificationClient
	) {
		this.labTestRepository = labTestRepository;
		this.labResultRepository = labResultRepository;
		this.mapper = mapper;
		this.billingClient = billingClient;
		this.notificationClient = notificationClient;
	}

	// CREATE LAB TEST
	@Override
	public List<LabTestResponse> createLabTests(CreateLabTestRequest request) {
		List<LabTest> savedTests = new ArrayList<>();
		BigDecimal totalFee = BigDecimal.ZERO;

		long count = labTestRepository.count();

		for (LabTestResponse testRequest : request.getTests()) {
			LabTest test = new LabTest();
			test.setPatientId(testRequest.getPatientId());
			test.setAppointmentId(testRequest.getAppointmentId());
			test.setTestName(testRequest.getTestName());
			test.setFee(testRequest.getFee());
			test.setStatus(LabTestStatus.PENDING);
			test.setCreatedAt(LocalDateTime.now());

			// Generate unique test code based on count
			String testCode = "T" + String.format("%03d", ++count);
			test.setTestCode(testCode);

			LabTest saved = labTestRepository.save(test);
			savedTests.add(saved);

			totalFee = totalFee.add(saved.getFee());
		}

		Long appointmentId = request.getAppointmentId();
		List<LabTestResponse> responses = savedTests
			.stream()
			.map(saved ->
				new LabTestResponse(
					saved.getId(),
					saved.getPatientId(),
					saved.getAppointmentId(),
					saved.getTestName(),
					saved.getTestCode(),
					saved.getStatus().name(),
					saved.getFee(),
					saved.getCreatedAt()
				)
			)
			.toList();

		// Call billing service once with aggregated fee
		billingClient.updateLabFee("ADMIN", appointmentId, totalFee, responses);
		return responses;
	}

	// READ PENDING LAB TESTS
	@Override
	public List<LabTestResponse> getPendingLabTests() {
		List<LabTestResponse> result = labTestRepository
			.findByStatus(LabTestStatus.PENDING)
			.stream()
			.map(mapper::toDto)
			.toList();
		return result;
	}

	// UPDATE LAB TEST STATUS - SAMPLE COLLECTED
	@Override
	public LabTestResponse collectSample(Long labTestId) {
		LabTest test = getLabTestOrThrow(labTestId);
		test.setStatus(LabTestStatus.SAMPLE_COLLECTED);
		test.setUpdatedAt(LocalDateTime.now());
		LabTest saved = labTestRepository.save(test);
		NotificationResponse notification = NotificationResponse
			.builder()
			.title("Lab Sample Collected")
			.message("Sample collected for test: " + saved.getTestName() + " (" + saved.getTestCode() + ")")
			.type(NotificationType.LAB)
			.build();
		createNotification(notification);
		return mapper.toDto(saved);
	}

	// UPDATE LAB TEST STATUS - IN PROGRESS
	@Override
	public LabTestResponse startTest(Long labTestId, String assignedTo) {
		LabTest test = getLabTestOrThrow(labTestId);
		test.setStatus(LabTestStatus.IN_PROGRESS);
		test.setUpdatedAt(LocalDateTime.now());
		test.setAssignedTo(assignedTo);
		LabTest saved = labTestRepository.save(test);
		NotificationResponse notification = NotificationResponse
			.builder()
			.title("Lab Test Started")
			.message("Lab test started for: " + saved.getTestName() + " (" + saved.getTestCode() + ")")
			.type(NotificationType.LAB)
			.build();
		createNotification(notification);
		return mapper.toDto(saved);
	}

	// UPLOAD LAB RESULT
	@Override
	public LabResultResponse uploadResult(Long labTestId, LabResultResponse resultDto) {
		LabTest labTest = getLabTestOrThrow(labTestId); // ensure test exists before creating result

		LabResult existingResult = labResultRepository.findByLabTestId(labTestId);
		if (existingResult != null) {
			throw new RuntimeException("Result already exists for this lab test");
		}
		LabResult result = new LabResult();
		result.setUnit(resultDto.getUnit());
		result.setNotes(resultDto.getNotes());
		result.setResultValue(resultDto.getResultValue());
		result.setIsAbnormal(resultDto.getIsAbnormal());
		result.setRecordedBy(resultDto.getRecordedBy());
		result.setFee(labTest.getFee());
		result.setReferenceRange(resultDto.getReferenceRange());
		result.setRecordedAt(LocalDateTime.now());

		LabResult savedResult = labResultRepository.save(result);
		NotificationResponse notification = NotificationResponse
			.builder()
			.title("Lab Result Uploaded")
			.message("Result uploaded for test: " + labTest.getTestName() + " (" + labTest.getTestCode() + ")")
			.type(NotificationType.LAB)
			.build();
		createNotification(notification);
		return mapper.toDto(savedResult);
	}

	// GET LAB RESULTS BY TEST ID
	@Override
	public LabResultResponse getResultsByLabTestId(Long labTestId) {
		getLabTestOrThrow(labTestId); // ensure test exists
		LabResult result = labResultRepository.findByLabTestId(labTestId);
		return mapper.toDto(result);
	}

	// GET LAB RESULTS BY PATIENT ID
	@Override
	public List<LabResultResponse> getResultsByPatientId(Long patientId) {
		List<LabTest> tests = labTestRepository.findByPatientId(patientId);
		if (tests.isEmpty()) {
			throw new LabTestNotFoundException(patientId);
		}
		return tests
			.stream()
			.map(test -> labResultRepository.findByLabTestId(test.getId()))
			.filter(Objects::nonNull)
			.map(result -> mapper.toDto(result))
			.toList();
	}

	@Override
	public List<LabTestResponse> getLabTestsByAppointmentId(Long appointmentId) {
		List<LabTest> tests = labTestRepository.findByAppointmentId(appointmentId);
		if (tests.isEmpty()) {
			throw new LabTestNotFoundException(appointmentId);
		}
		return tests.stream().map(test -> mapper.toDto(test)).toList();
	}

	// PRIVATE HELPER
	private LabTest getLabTestOrThrow(Long id) {
		return labTestRepository.findById(id).orElseThrow(() -> new LabTestNotFoundException(id));
		//        private LabResult getLabResultOrThrow(Long id){
		//            return labResultRepository.findById(id)
		//                    .orElseThrow(() -> new LabResultNotFoundException(id));
		//        }
	}

	@CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "createNotificationFallback")
	private NotificationResponse createNotification(NotificationResponse notification) {
		notificationClient.send(notification);
		return notification;
	}

	private NotificationResponse createNotificationFallback(NotificationResponse notification, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for notification: " + t.getMessage());
		return notification;
	}

	@CircuitBreaker(name = "billingServiceCB", fallbackMethod = "initiateInvoiceFallback")
	private InvoiceUpdateResponse createInvoice(
		Long appointmentId,
		BigDecimal labFee,
		List<LabTestResponse> labTestResponses
	) {
		return billingClient.updateLabFee("ADMIN", appointmentId, labFee, labTestResponses);
	}

	private InvoiceUpdateResponse initiateInvoiceFallback(Long patientId, Long appointmentId, Throwable t) {
		System.err.println("Circuit breaker fallback triggered for billing service: " + t.getMessage());
		return null;
	}
}
