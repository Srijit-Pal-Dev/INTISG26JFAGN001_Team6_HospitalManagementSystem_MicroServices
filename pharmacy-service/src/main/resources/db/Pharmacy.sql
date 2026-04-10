CREATE TABLE medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(150) NOT NULL,
    category VARCHAR(100),
    manufacturer VARCHAR(150),
    unit VARCHAR(50),
    dosage_strength VARCHAR(50),

    price_per_unit DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL CHECK (stock_quantity >= 0),

    requires_prescription BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uq_medicine_name_manufacturer (name, manufacturer),
    INDEX idx_medicine_name (name),
    INDEX idx_medicine_category (category)
);

CREATE TABLE dispense_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    prescription_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL,

    medicine_id BIGINT NOT NULL,
    medicine_name VARCHAR(150) NOT NULL,

    quantity INT NOT NULL CHECK (quantity > 0),

    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    dispensed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_dispense_status (status),
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_patient_id (patient_id),

    CONSTRAINT fk_dispense_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines(id)
        ON DELETE RESTRICT
);

ALTER TABLE dispense_requests
ADD CONSTRAINT chk_dispense_status
CHECK (status IN ('PENDING', 'DISPENSED'));