package com.cognizant.pharmacyService.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class MedicineRequest {

	@NotBlank(message = "Medicine name is required")
	private String name;

	@NotBlank(message = "Category is required")
	private String category;

	@NotBlank(message = "Manufacturer is required")
	private String manufacturer;

	private String unit;
	
	@NotBlank(message = "Dosage is required")
	private String dosageStrength;

	@NotNull(message = "Price is required")
	@Min(value = 1, message = "Price must be greater than 0")
	private BigDecimal pricePerUnit;

	@NotNull(message = "Stock quantity is required")
	@Min(value = 0, message = "Stock cannot be negative")
	private Integer stockQuantity;

	private Boolean requiresPrescription;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDosageStrength() {
		return dosageStrength;
	}

	public void setDosageStrength(String dosageStrength) {
		this.dosageStrength = dosageStrength;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public Boolean getRequiresPrescription() {
		return requiresPrescription;
	}

	public void setRequiresPrescription(Boolean requiresPrescription) {
		this.requiresPrescription = requiresPrescription;
	}
}