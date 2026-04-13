package com.cognizant.pharmacyService.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cognizant.pharmacyService.domain.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

	List<Medicine> findByNameContainingIgnoreCase(String name);

	@Query("""
			    SELECT m FROM Medicine m
			    WHERE m.stockQuantity > 0
			    ORDER BY m.name ASC
			""")
	List<Medicine> findAvailableMedicines();
}