package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private TokenService tokenService;

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> addDoctor(Doctor doctor) {
        try {
            if (doctor.getName() == null || doctor.getName().isBlank())
                return ResponseEntity.status(400).body(Map.of("message", "Doctor name is required"));
            if (doctor.getSpecialty() == null || doctor.getSpecialty().isBlank())
                return ResponseEntity.status(400).body(Map.of("message", "Specialty is required"));
            if (doctor.getEmail() == null || doctor.getEmail().isBlank())
                return ResponseEntity.status(400).body(Map.of("message", "Email is required"));
            if (doctor.getPassword() == null || doctor.getPassword().isBlank())
                return ResponseEntity.status(400).body(Map.of("message", "Password is required"));
            Doctor saved = doctorRepository.save(doctor);
            return ResponseEntity.ok(Map.of("message", "Doctor added successfully", "id", saved.getId()));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate"))
                return ResponseEntity.status(400).body(Map.of("message", "Email already exists"));
            return ResponseEntity.status(500).body(Map.of("message", "Failed to add doctor: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateDoctor(Long id, Doctor updatedDoctor) {
        Optional<Doctor> opt = doctorRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        Doctor existing = opt.get();
        if (updatedDoctor.getName() != null) existing.setName(updatedDoctor.getName());
        if (updatedDoctor.getSpecialty() != null) existing.setSpecialty(updatedDoctor.getSpecialty());
        if (updatedDoctor.getEmail() != null) existing.setEmail(updatedDoctor.getEmail());
        if (updatedDoctor.getPassword() != null) existing.setPassword(updatedDoctor.getPassword());
        if (updatedDoctor.getAvailableTimes() != null) existing.setAvailableTimes(updatedDoctor.getAvailableTimes());
        doctorRepository.save(existing);
        return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
    }

    @Transactional
    public ResponseEntity<?> deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id))
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        List<com.project.back_end.models.Appointment> appts = appointmentRepository.findByDoctorId(id);
        appointmentRepository.deleteAll(appts);
        doctorRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }

    @Transactional(readOnly = true)
    public List<Doctor> searchDoctors(String name, String specialty, String time) {
        List<Doctor> results;
        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            results = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (name != null && !name.isEmpty()) {
            results = doctorRepository.findByNameContainingIgnoreCase(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            results = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        } else {
            results = doctorRepository.findAll();
        }
        if (time != null && !time.isEmpty()) {
            final String t = time.trim();
            results = results.stream()
                    .filter(d -> d.getAvailableTimes() != null &&
                            d.getAvailableTimes().stream().anyMatch(s -> s.equalsIgnoreCase(t)))
                    .collect(Collectors.toList());
        }
        return results;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> loginDoctor(String email, String password) {
        if (email == null || password == null)
            return ResponseEntity.status(400).body(Map.of("message", "Email and password required"));
        Optional<Doctor> opt = doctorRepository.findByEmailAndPassword(email, password);
        if (opt.isPresent()) {
            Doctor d = opt.get();
            String token = tokenService.generateToken(d.getEmail(), "DOCTOR");
            return ResponseEntity.ok(Map.of("token", token, "role", "DOCTOR", "doctorId", d.getId()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }
}