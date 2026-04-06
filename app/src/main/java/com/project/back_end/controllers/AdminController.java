package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login) {
        return service.validateAdmin(login.getUsername(), login.getPassword());
    }
}
