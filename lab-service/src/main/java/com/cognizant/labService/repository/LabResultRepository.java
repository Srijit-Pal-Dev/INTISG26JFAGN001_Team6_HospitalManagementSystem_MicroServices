package com.cognizant.labService.repository;

import com.cognizant.labService.domain.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LabResultRepository extends JpaRepository<LabResult, Long>{
    LabResult findByLabTestId(Long labTestId);

}
