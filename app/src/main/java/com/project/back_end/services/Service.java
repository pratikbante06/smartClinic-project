package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    // 1. Validate JWT token for a given role
    public ResponseEntity<?> validateToken(String token, String role) {
        if (tokenService.validateToken(token, role)) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
    }

    // 2. Validate admin login and return JWT token
    @Transactional(readOnly = true)
    public ResponseEntity<?> validateAdmin(String username, String password) {
        try {
            Optional<Admin> admin = adminRepository.findByUsername(username);
            if (admin.isPresent() && admin.get().getPassword().equals(password)) {
                String token = tokenService.generateToken(username, "ADMIN");
                return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
            }
            return ResponseEntity.status(401).body(Map.of("message", "Invalid admin credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    // 3. Filter doctors by name and specialty
    @Transactional(readOnly = true)
    public List<Doctor> filterDoctor(String name, String specialty, String time) {
        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (name != null && !name.isEmpty()) {
            return doctorRepository.findByNameContainingIgnoreCase(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            return doctorRepository.findBySpecialtyIgnoreCase(specialty);
        }
        return doctorRepository.findAll();
    }

    // 4. Validate that requested appointment time is available for doctor
    @Transactional(readOnly = true)
    public int validateAppointment(Long doctorId, LocalDate date, String requestedTime) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty()) return -1;
        boolean available = doctor.get().getAvailableTimes()
                .stream().anyMatch(t -> t.startsWith(requestedTime));
        return available ? 1 : 0;
    }

    // 5. Check if patient email/phone already exists
    @Transactional(readOnly = true)
    public boolean validatePatient(String email, String phone) {
        return !patientRepository.existsByEmailOrPhone(email, phone);
    }

    // 6. Validate patient login and return JWT token
    @Transactional(readOnly = true)
    public ResponseEntity<?> validatePatientLogin(String email, String password) {
        try {
            Optional<Patient> opt = patientRepository.findByEmailAndPassword(email, password);
            if (opt.isPresent()) {
                Patient p = opt.get();
                String token = tokenService.generateToken(p.getEmail(), "PATIENT");
                return ResponseEntity.ok(Map.of("token", token, "role", "PATIENT", "patientId", p.getId()));
            }
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Server error"));
        }
    }

    // 7. Filter patient appointment history
    @Transactional(readOnly = true)
    public ResponseEntity<?> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.getUsernameFromToken(token);
            Optional<Patient> opt = patientRepository.findByEmail(email);
            if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Patient not found"));
            Long patientId = opt.get().getId();

            List<AppointmentDTO> result;
            boolean hasCondition = condition != null && !condition.isEmpty();
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
            return ResponseEntity.status(500).body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }
}
