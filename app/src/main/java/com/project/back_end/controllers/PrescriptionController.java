package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired private PrescriptionService prescriptionService;
    @Autowired private TokenService tokenService;

    @PostMapping("/add")
    public ResponseEntity<?> addPrescription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Prescription prescription) {
        String token = authHeader.replace("Bearer ", "");
        if (!tokenService.validateToken(token, "DOCTOR"))
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        String doctorName = tokenService.getUsernameFromToken(token);
        if (prescription.getDoctorName() == null || prescription.getDoctorName().isBlank())
            prescription.setDoctorName(doctorName);
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getByAppointment(
            @PathVariable Long appointmentId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null) {
            String token = authHeader.replace("Bearer ", "");
            boolean isDoctor = tokenService.validateToken(token, "DOCTOR");
            boolean isPatient = tokenService.validateToken(token, "PATIENT");
            if (!isDoctor && !isPatient)
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        return prescriptionService.getPrescription(appointmentId);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getByPatient(
            @PathVariable Long patientId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return prescriptionService.getPrescriptionsByPatient(patientId);
    }
}
