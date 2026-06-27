package com.proofit.proofit.controller;

import com.proofit.proofit.dto.AuthResponse;
import com.proofit.proofit.dto.LoginRequest;
import com.proofit.proofit.dto.RegisterRequest;
import com.proofit.proofit.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("error", e.getMessage(), "cause",
                            e.getCause() != null ? e.getCause().getMessage() : "none")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    java.util.Map.of("error", e.getMessage(), "cause",
                            e.getCause() != null ? e.getCause().getMessage() : "none")
            );
        }
    }
}