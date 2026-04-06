-- Seed admin
INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'admin123');

-- Seed doctors
INSERT IGNORE INTO doctor (name, specialty, email, password) VALUES
('Dr. Emily Adams',  'Cardiologist',   'dr.adams@example.com',   'password123'),
('Dr. Mark Johnson', 'Neurologist',     'dr.johnson@example.com', 'password123'),
('Dr. Sarah Lee',    'Orthopedist',     'dr.lee@example.com',     'password123'),
('Dr. Tom Wilson',   'Pediatrician',    'dr.wilson@example.com',  'password123'),
('Dr. Alice Brown',  'Dermatologist',   'dr.brown@example.com',   'password123'),
('Dr. Taylor Grant', 'Cardiologist',    'dr.taylor@example.com',  'password123');

-- Seed available times using subquery for correct IDs
INSERT IGNORE INTO doctor_available_times (doctor_id, available_time)
SELECT id, '09:00-10:00' FROM doctor WHERE email='dr.adams@example.com' UNION ALL
SELECT id, '10:00-11:00' FROM doctor WHERE email='dr.adams@example.com' UNION ALL
SELECT id, '11:00-12:00' FROM doctor WHERE email='dr.adams@example.com' UNION ALL
SELECT id, '14:00-15:00' FROM doctor WHERE email='dr.adams@example.com' UNION ALL
SELECT id, '10:00-11:00' FROM doctor WHERE email='dr.johnson@example.com' UNION ALL
SELECT id, '11:00-12:00' FROM doctor WHERE email='dr.johnson@example.com' UNION ALL
SELECT id, '14:00-15:00' FROM doctor WHERE email='dr.johnson@example.com' UNION ALL
SELECT id, '15:00-16:00' FROM doctor WHERE email='dr.johnson@example.com' UNION ALL
SELECT id, '09:00-10:00' FROM doctor WHERE email='dr.lee@example.com' UNION ALL
SELECT id, '11:00-12:00' FROM doctor WHERE email='dr.lee@example.com' UNION ALL
SELECT id, '14:00-15:00' FROM doctor WHERE email='dr.lee@example.com' UNION ALL
SELECT id, '16:00-17:00' FROM doctor WHERE email='dr.lee@example.com' UNION ALL
SELECT id, '09:00-10:00' FROM doctor WHERE email='dr.wilson@example.com' UNION ALL
SELECT id, '10:00-11:00' FROM doctor WHERE email='dr.wilson@example.com' UNION ALL
SELECT id, '15:00-16:00' FROM doctor WHERE email='dr.wilson@example.com' UNION ALL
SELECT id, '16:00-17:00' FROM doctor WHERE email='dr.wilson@example.com' UNION ALL
SELECT id, '09:00-10:00' FROM doctor WHERE email='dr.brown@example.com' UNION ALL
SELECT id, '14:00-15:00' FROM doctor WHERE email='dr.brown@example.com' UNION ALL
SELECT id, '15:00-16:00' FROM doctor WHERE email='dr.brown@example.com' UNION ALL
SELECT id, '09:00-10:00' FROM doctor WHERE email='dr.taylor@example.com' UNION ALL
SELECT id, '10:00-11:00' FROM doctor WHERE email='dr.taylor@example.com' UNION ALL
SELECT id, '11:00-12:00' FROM doctor WHERE email='dr.taylor@example.com' UNION ALL
SELECT id, '14:00-15:00' FROM doctor WHERE email='dr.taylor@example.com';

-- Seed patients
INSERT IGNORE INTO patient (name, email, password, phone, address) VALUES
('John Smith',   'john@example.com',  'password123', '9876543210', '123 Main St'),
('Alice Brown',  'alice@example.com', 'password123', '9876543211', '456 Oak Ave'),
('Bob Wilson',   'bob@example.com',   'password123', '9876543212', '789 Pine Rd'),
('Carol Davis',  'carol@example.com', 'password123', '9876543213', '321 Elm St'),
('David Miller', 'david@example.com', 'password123', '9876543214', '654 Maple Dr');

-- Seed appointments using subqueries for correct IDs
INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2026-06-01', '09:00:00', 0
FROM doctor d, patient p WHERE d.email='dr.adams@example.com' AND p.email='john@example.com';

INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2026-06-01', '10:00:00', 0
FROM doctor d, patient p WHERE d.email='dr.johnson@example.com' AND p.email='alice@example.com';

INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2026-06-02', '11:00:00', 0
FROM doctor d, patient p WHERE d.email='dr.lee@example.com' AND p.email='bob@example.com';

INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2025-05-22', '10:00:00', 1
FROM doctor d, patient p WHERE d.email='dr.adams@example.com' AND p.email='alice@example.com';

INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2025-05-22', '14:00:00', 1
FROM doctor d, patient p WHERE d.email='dr.johnson@example.com' AND p.email='john@example.com';

INSERT IGNORE INTO appointment (doctor_id, patient_id, appointment_date, appointment_time, status)
SELECT d.id, p.id, '2025-05-23', '09:00:00', 1
FROM doctor d, patient p WHERE d.email='dr.lee@example.com' AND p.email='john@example.com';
