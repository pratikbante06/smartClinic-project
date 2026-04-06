package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private TokenService tokenService;

    // POST /api/doctor/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return doctorService.loginDoctor(login.getUsername(), login.getPassword());
    }

    // GET /api/doctor/all
    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // POST /api/doctor/add  (admin only)
    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(
            @RequestHeader("Authorization") String token,
            @RequestBody Doctor doctor) {
        if (!tokenService.validateToken(token, "ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        return doctorService.addDoctor(doctor);
    }

    // PUT /api/doctor/update/{id}  (admin only)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDoctor(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Doctor doctor) {
        if (!tokenService.validateToken(token, "ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        return doctorService.updateDoctor(id, doctor);
    }

    // DELETE /api/doctor/delete/{id}  (admin only)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDoctor(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        if (!tokenService.validateToken(token, "ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        return doctorService.deleteDoctor(id);
    }

    // GET /api/doctor/search?name=&specialty=&time=
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String time) {
        return ResponseEntity.ok(doctorService.searchDoctors(name, specialty, time));
    }
}