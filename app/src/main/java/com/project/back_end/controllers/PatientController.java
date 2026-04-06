package com.project.back_end.controllers;

import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired private PatientRepository patientRepository;
    @Autowired private PatientService patientService;
    @Autowired private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@RequestBody Patient patient) {
        // Manual validation - more reliable than @Valid with custom field access
        if (patient.getName() == null || patient.getName().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Name is required"));
        if (patient.getEmail() == null || patient.getEmail().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Email is required"));
        if (patient.getPassword() == null || patient.getPassword().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Password is required"));
        if (patient.getPhone() == null || patient.getPhone().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Phone is required"));
        if (patient.getAddress() == null || patient.getAddress().isBlank())
            return ResponseEntity.status(400).body(Map.of("message", "Address is required"));
        return patientService.createPatient(patient);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@RequestBody Map<String, String> body) {
        return patientService.loginPatient(body.get("email"), body.get("password"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getPatientDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        if (!tokenService.validateToken(token, "PATIENT"))
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        return patientService.getPatientDetails(token);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        return patientRepository.findById(id)
                .map(p -> (ResponseEntity<?>) ResponseEntity.ok(p))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Patient deleted"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterAppointments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String doctorName) {
        String token = authHeader.replace("Bearer ", "");
        if (!tokenService.validateToken(token, "PATIENT"))
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        String email = tokenService.getUsernameFromToken(token);
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isEmpty())
            return ResponseEntity.status(404).body(Map.of("message", "Patient not found"));
        return patientService.filterPatientAppointments(patient.get().getId(), condition, doctorName);
    }
}
