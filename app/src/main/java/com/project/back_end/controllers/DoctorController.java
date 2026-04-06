package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        // Not using @Valid here - we handle null checks manually for better error messages
        if (login.getUsername() == null || login.getUsername().isBlank() ||
            login.getPassword() == null || login.getPassword().isBlank()) {
            return ResponseEntity.status(400).body(Map.of("message", "Username and password required"));
        }
        Optional<Doctor> doctor = doctorService.findByNameAndPassword(login.getUsername(), login.getPassword());
        if (doctor.isPresent()) {
            String token = tokenService.generateToken(doctor.get().getName(), "DOCTOR");
            return ResponseEntity.ok(Map.of("token", token, "role", "DOCTOR", "doctorId", doctor.get().getId()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@RequestBody Doctor doctor) {
        // Manual validation for better error messages
        if (doctor.getName() == null || doctor.getName().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Doctor name is required"));
        if (doctor.getSpecialty() == null || doctor.getSpecialty().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Specialty is required"));
        if (doctor.getEmail() == null || doctor.getEmail().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Email is required"));
        if (doctor.getPassword() == null || doctor.getPassword().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Password is required"));
        try {
            Doctor saved = doctorService.addDoctor(doctor);
            return ResponseEntity.ok(Map.of("message", "Doctor added successfully", "id", saved.getId()));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate")) {
                return ResponseEntity.status(400).body(Map.of("message", "Email already exists"));
            }
            return ResponseEntity.status(500).body(Map.of("message", "Failed to add doctor: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        boolean deleted = doctorService.deleteDoctor(id);
        if (deleted) return ResponseEntity.ok(Map.of("message", "Doctor removed"));
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String time) {
        return ResponseEntity.ok(doctorService.searchDoctors(name, specialty));
    }
}
