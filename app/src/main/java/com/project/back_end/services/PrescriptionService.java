package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Transactional
    public ResponseEntity<?> savePrescription(Prescription prescription) {
        try {
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existing.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("message", "Prescription already exists for this appointment"));
            }
            prescriptionRepository.save(prescription);
            return ResponseEntity.status(201).body(Map.of("message", "Prescription saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save prescription: " + e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            return ResponseEntity.ok(Map.of("prescription", prescriptions));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch prescription"));
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPrescriptionsByPatient(Long patientId) {
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to fetch prescriptions"));
        }
    }
}
