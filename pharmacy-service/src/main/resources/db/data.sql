INSERT INTO medicines (
    id,
    name,
    category,
    manufacturer,
    unit,
    dosage_strength,
    price_per_unit,
    stock_quantity,
    requires_prescription
) VALUES
(1, 'Paracetamol', 'Painkiller', 'Cipla', 'Tablet', '500mg', 2.50, 200, TRUE),
(2, 'Amoxicillin', 'Antibiotic', 'Sun Pharma', 'Capsule', '250mg', 6.75, 120, TRUE),
(3, 'Cetirizine', 'Antihistamine', 'Dr Reddys', 'Tablet', '10mg', 1.80, 300, FALSE),
(4, 'Metformin', 'Diabetes', 'Lupin', 'Tablet', '500mg', 3.20, 150, TRUE),
(5, 'ORS Powder', 'Electrolyte', 'Pfizer', 'Sachet', '21g', 12.00, 80, FALSE);

INSERT INTO dispense_requests (
    id,
    prescription_id,
    patient_id,
    appointment_id,
    medicine_id,
    medicine_name,
    quantity,
    unit_price,
    total_price,
    status
) VALUES
(1, 101, 201, 301, 1, 'Paracetamol', 10, 2.50, 25.00, 'PENDING'),
(2, 102, 202, 302, 2, 'Amoxicillin', 5, 6.75, 33.75, 'PENDING');