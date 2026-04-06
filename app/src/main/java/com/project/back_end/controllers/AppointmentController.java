package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private TokenService tokenService;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        String token = authHeader.replace("Bearer ", "");
        if (!tokenService.validateToken(token, "PATIENT"))
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));

        try {
            // Extract IDs from nested objects
            @SuppressWarnings("unchecked")
            Map<String, Object> doctorMap = (Map<String, Object>) body.get("doctor");
            @SuppressWarnings("unchecked")
            Map<String, Object> patientMap = (Map<String, Object>) body.get("patient");

            Long doctorId = Long.valueOf(doctorMap.get("id").toString());
            Long patientId = Long.valueOf(patientMap.get("id").toString());

            Optional<Doctor> doctor = doctorRepository.findById(doctorId);
            Optional<Patient> patient = patientRepository.findById(patientId);

            if (doctor.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
            if (patient.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Patient not found"));

            Appointment appointment = new Appointment();
            appointment.setDoctor(doctor.get());
            appointment.setPatient(patient.get());
            appointment.setAppointmentDate(java.time.LocalDate.parse(body.get("appointmentDate").toString()));
            appointment.setAppointmentTime(java.time.LocalTime.parse(body.get("appointmentTime").toString()));
            appointment.setStatus(0);

            return appointmentService.bookAppointment(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Booking failed: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Appointment appointment = new Appointment();

            if (body.containsKey("doctor")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> doctorMap = (Map<String, Object>) body.get("doctor");
                Long doctorId = Long.valueOf(doctorMap.get("id").toString());
                doctorRepository.findById(doctorId).ifPresent(appointment::setDoctor);
            }

            if (body.containsKey("appointmentDate"))
                appointment.setAppointmentDate(java.time.LocalDate.parse(body.get("appointmentDate").toString()));
            if (body.containsKey("appointmentTime"))
                appointment.setAppointmentTime(java.time.LocalTime.parse(body.get("appointmentTime").toString()));

            return appointmentService.updateAppointment(id, appointment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Update failed: " + e.getMessage()));
        }
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorAppointments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = authHeader.replace("Bearer ", "");
        if (!tokenService.validateToken(token, "DOCTOR"))
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        String doctorName = tokenService.getUsernameFromToken(token);
        Optional<Doctor> doc = doctorRepository.findByNameContainingIgnoreCase(doctorName).stream().findFirst();
        if (doc.isEmpty())
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        List<AppointmentDTO> appts = appointmentService.getAppointmentsByDoctor(doc.get().getId(), patientName, date);
        return ResponseEntity.ok(appts);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        boolean cancelled = appointmentService.cancelAppointment(id);
        if (cancelled) return ResponseEntity.ok(Map.of("message", "Appointment cancelled"));
        return ResponseEntity.notFound().build();
    }
}
