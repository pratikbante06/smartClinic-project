package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AppointmentService appointmentService;

    @Transactional
    public ResponseEntity<?> createPatient(Patient patient) {
        try {
            if (patientRepository.existsByEmailOrPhone(patient.getEmail(), patient.getPhone())) {
                return ResponseEntity.status(400).body(Map.of("message", "Email or phone already registered"));
            }
            Patient saved = patientRepository.save(patient);
            return ResponseEntity.ok(Map.of("message", "Patient registered successfully", "id", saved.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> loginPatient(String email, String password) {
        try {
            if (email == null || password == null) {
                return ResponseEntity.status(400).body(Map.of("message", "Email and password required"));
            }
            Optional<Patient> opt = patientRepository.findByEmailAndPassword(email, password);
            if (opt.isPresent()) {
                Patient p = opt.get();
                String token = tokenService.generateToken(p.getEmail(), "PATIENT");
                return ResponseEntity.ok(Map.of("token", token, "role", "PATIENT", "patientId", p.getId()));
            }
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPatientAppointments(Long patientId) {
        try {
            List<AppointmentDTO> dtos = appointmentService.getAppointmentsByPatient(patientId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch appointments"));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterPatientAppointments(Long patientId, String condition, String doctorName) {
        try {
            List<AppointmentDTO> result;
            boolean hasCondition = condition != null && !condition.isEmpty() && !condition.equals("allAppointments");
            boolean hasDoctor = doctorName != null && !doctorName.isEmpty();

            if (hasCondition && hasDoctor) {
                int status = condition.equals("past") ? 1 : 0;
                result = appointmentService.filterByDoctorAndStatus(patientId, doctorName, status);
            } else if (hasCondition) {
                int status = condition.equals("past") ? 1 : 0;
                result = appointmentService.filterByStatus(patientId, status);
            } else if (hasDoctor) {
                result = appointmentService.filterByDoctor(patientId, doctorName);
            } else {
                result = appointmentService.getAppointmentsByPatient(patientId);
            }
            return ResponseEntity.ok(Map.of("appointments", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to filter appointments: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPatientDetails(String token) {
        try {
            String email = tokenService.getUsernameFromToken(token);
            Optional<Patient> opt = patientRepository.findByEmail(email);
            return opt.map(p -> (ResponseEntity<?>) ResponseEntity.ok(p))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch patient details"));
        }
    }
}
