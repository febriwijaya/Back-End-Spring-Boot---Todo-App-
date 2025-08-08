package com.myproject.todo_management.controller;

import com.myproject.todo_management.dto.JwtAuthResponse;
import com.myproject.todo_management.dto.LoginDto;
import com.myproject.todo_management.dto.RegisterDto;
import com.myproject.todo_management.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j //-- tambah logger untuk tangkap error
public class AuthController {

    private AuthService authService;

    // Build Register REST API
    // Hanya ADMIN yang boleh register user baru
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        try {
            String response = authService.register(registerDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponse jwtAuthResponse = authService.login(loginDto);
            return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
        } catch (Exception e) {
            log.error("There is an error", e); // log error + stack trace
            throw e;
        }

    }
}
