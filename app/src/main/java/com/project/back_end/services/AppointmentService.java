package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private DoctorRepository doctorRepository;

    @Transactional
    public ResponseEntity<?> bookAppointment(Appointment appointment) {
        try {
            Appointment saved = appointmentRepository.save(appointment);
            return ResponseEntity.ok(Map.of("message", "Appointment booked successfully", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to book appointment: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateAppointment(Long id, Appointment appointment) {
        try {
            Optional<Appointment> existing = appointmentRepository.findById(id);
            if (existing.isEmpty()) return ResponseEntity.notFound().build();

            Appointment toUpdate = existing.get();
            if (appointment.getAppointmentDate() != null)
                toUpdate.setAppointmentDate(appointment.getAppointmentDate());
            if (appointment.getAppointmentTime() != null)
                toUpdate.setAppointmentTime(appointment.getAppointmentTime());

            // If doctor is set, re-fetch from DB to avoid detached entity issues
            if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
                Optional<Doctor> freshDoctor = doctorRepository.findById(appointment.getDoctor().getId());
                freshDoctor.ifPresent(toUpdate::setDoctor);
            }

            appointmentRepository.save(toUpdate);
            return ResponseEntity.ok(Map.of("message", "Appointment updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update appointment: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId, String patientName, LocalDate date) {
        List<Appointment> appointments;
        if (date != null && patientName != null && !patientName.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date)
                    .stream()
                    .filter(a -> a.getPatient() != null &&
                            a.getPatient().getName().toLowerCase().contains(patientName.toLowerCase()))
                    .collect(Collectors.toList());
        } else if (date != null) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        } else if (patientName != null && !patientName.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndPatientNameContainingIgnoreCase(doctorId, patientName);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
        }
        return appointments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByStatus(Long patientId, int status) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(Long patientId, String doctorName) {
        return appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(patientId, doctorName)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndStatus(Long patientId, String doctorName, int status) {
        return appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(patientId, doctorName, status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean cancelAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private AppointmentDTO toDTO(Appointment a) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(a.getId());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setStatus(a.getStatus());
        if (a.getPatient() != null) {
            dto.setPatientName(a.getPatient().getName());
            dto.setPatientId(a.getPatient().getId());
        }
        if (a.getDoctor() != null) {
            dto.setDoctorName(a.getDoctor().getName());
            dto.setDoctorId(a.getDoctor().getId());
        }
        return dto;
    }
}
