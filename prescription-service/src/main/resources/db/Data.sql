-- ================================
-- DOCTORS
-- ================================
INSERT INTO doctors (
    user_id,
    full_name,
    specialty,
    qualification,
    experience_years,
    consultation_fee
) VALUES
(101, 'Dr. Arun Kumar', 'General Medicine', 'MBBS', 12, 500.00),
(102, 'Dr. Meena Rao', 'Cardiology', 'MBBS, MD', 15, 800.00);

-- ================================
-- PRESCRIPTIONS
-- ================================
-- Appointment ID = 1
INSERT INTO prescriptions (
    appointment_id,
    doctor_id,
    patient_id,
    diagnosis,
    doctor_notes,
    lab_required,
    created_at
) VALUES (
    1,
    1,      -- doctor_id for user_id=101
    201,
    'Fever',
    'Take rest and maintain hydration',
    1,
    NOW()
);

-- Appointment ID = 2
INSERT INTO prescriptions (
    appointment_id,
    doctor_id,
    patient_id,
    diagnosis,
    doctor_notes,
    lab_required,
    created_at
) VALUES (
    2,
    2,      -- doctor_id for user_id=102
    202,
    'Chest pain',
    'Avoid physical exertion',
    1,
    NOW()
);

-- ================================
-- PRESCRIPTION MEDICINES
-- ================================
INSERT INTO prescription_medicines (
    prescription_id,
    medicine_id,
    medicine_name,
    dosage,
    frequency,
    duration,
    instructions
) VALUES
(1, 1, 'Paracetamol', '500 mg', 'Twice a day', '5 days', 'After food'),
(2, 2, 'Aspirin', '75 mg', 'Once a day', '10 days', 'Morning only');

-- ================================
-- PRESCRIPTION LAB TESTS
-- ================================
INSERT INTO prescription_lab_tests (
    prescription_id,
    test_code,
    test_name,
    notes
) VALUES
(1, 'CBC', 'Complete Blood Count', 'No fasting required'),
(2, 'ECG', 'Electrocardiogram', 'Rest before test');
