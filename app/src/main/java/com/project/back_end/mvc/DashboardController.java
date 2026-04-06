package com.project.back_end.mvc;

import com.project.back_end.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    // BUG FIX: JWT tokens contain dots (.) which Spring MVC treats as file extensions
    // and strips from path variables. The regex :.+ forces Spring to capture the full token.
    @GetMapping("/adminDashboard/{token:.+}")
    public String adminDashboard(@PathVariable String token) {
        if (tokenService.validateToken(token, "ADMIN")) {
            return "admin/adminDashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token:.+}")
    public String doctorDashboard(@PathVariable String token) {
        if (tokenService.validateToken(token, "DOCTOR")) {
            return "doctor/doctorDashboard";
        }
        return "redirect:/";
    }
}
