package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Appointment> findByDoctorIdAndPatientNameContainingIgnoreCase(@Param("doctorId") Long doctorId, @Param("name") String name);

    List<Appointment> findByPatientIdAndStatus(Long patientId, int status);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%'))")
    List<Appointment> findByPatientIdAndDoctorNameContainingIgnoreCase(@Param("patientId") Long patientId, @Param("doctorName") String doctorName);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) AND a.status = :status")
    List<Appointment> findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(@Param("patientId") Long patientId, @Param("doctorName") String doctorName, @Param("status") int status);
}
