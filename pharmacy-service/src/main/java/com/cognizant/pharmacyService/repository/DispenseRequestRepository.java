package com.cognizant.pharmacyService.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cognizant.pharmacyService.domain.DispenseRequest;
import com.cognizant.pharmacyService.domain.DispenseStatus;

@Repository
public interface DispenseRequestRepository extends JpaRepository<DispenseRequest, Long> {

    List<DispenseRequest> findByStatus(DispenseStatus status);

    List<DispenseRequest> findByAppointmentId(Long appointmentId);

    @Query("""
			    SELECT SUM(d.totalPrice)
			    FROM DispenseRequest d
			    WHERE d.appointmentId = :appointmentId
			    AND d.status = 'DISPENSED'
			""")
    BigDecimal calculateTotalMedicineFee(@Param("appointmentId") Long appointmentId);

    List<DispenseRequest> findByAppointmentIdAndStatus(Long appointmentId, DispenseStatus status);

    List<DispenseRequest> findByPrescriptionIdAndStatus(Long prescriptionId, DispenseStatus status);
}