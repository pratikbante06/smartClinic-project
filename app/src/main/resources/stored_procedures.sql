DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor;
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth;
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear;

DELIMITER //

CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT d.id AS doctor_id,
           d.name AS doctor_name,
           d.specialty,
           p.name AS patient_name,
           p.phone AS patient_phone,
           a.appointment_time,
           a.status,
           report_date AS report_date
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    JOIN patient p ON a.patient_id = p.id
    WHERE a.appointment_date = report_date
    ORDER BY d.name ASC, a.appointment_time ASC;
END //

CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN report_month INT, IN report_year INT)
BEGIN
    SELECT d.id AS doctor_id,
           d.name AS doctor_name,
           d.specialty,
           COUNT(a.id) AS patients_seen,
           report_month AS month,
           report_year AS year
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE MONTH(a.appointment_date) = report_month
      AND YEAR(a.appointment_date) = report_year
    GROUP BY d.id, d.name, d.specialty
    ORDER BY patients_seen DESC
    LIMIT 1;
END //

CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN report_year INT)
BEGIN
    SELECT d.id AS doctor_id,
           d.name AS doctor_name,
           d.specialty,
           COUNT(a.id) AS patients_seen,
           report_year AS year
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_date) = report_year
    GROUP BY d.id, d.name, d.specialty
    ORDER BY patients_seen DESC
    LIMIT 1;
END //

DELIMITER ;

CALL GetDailyAppointmentReportByDoctor('2026-06-01');
CALL GetDoctorWithMostPatientsByMonth(6, 2026);
CALL GetDoctorWithMostPatientsByYear(2026);