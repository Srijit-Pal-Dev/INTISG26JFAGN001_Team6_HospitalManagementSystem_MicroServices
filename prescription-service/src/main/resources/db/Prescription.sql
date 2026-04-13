CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    qualification VARCHAR(100),
    experience_years INT,
    consultation_fee DECIMAL(10,2),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    appointment_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,

    diagnosis TEXT,
    doctor_notes TEXT,
    lab_required BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_prescription_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE prescription_medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT,
    medicine_name VARCHAR(100) NOT NULL,

    dosage VARCHAR(50),
    frequency VARCHAR(50),
    duration VARCHAR(50),
    instructions VARCHAR(255),

    CONSTRAINT fk_prescription_medicine
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(id)
        ON DELETE CASCADE
);

CREATE TABLE prescription_lab_tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    prescription_id BIGINT NOT NULL,

    test_code VARCHAR(50),
    test_name VARCHAR(100) NOT NULL,
    notes VARCHAR(255),

    CONSTRAINT fk_prescription_lab
        FOREIGN KEY (prescription_id) REFERENCES prescriptions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_prescription_appointment
ON prescriptions (appointment_id);

CREATE INDEX idx_prescription_patient
ON prescriptions (patient_id);

CREATE INDEX idx_prescription_doctor
ON prescriptions (doctor_id);

ALTER TABLE prescriptions
ADD CONSTRAINT uk_prescription_appointment UNIQUE (appointment_id);