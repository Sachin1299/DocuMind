package com.sachin.documind.controller;

import com.sachin.documind.dto.JwtAuthResponse;
import com.sachin.documind.dto.LoginRequest;
import com.sachin.documind.dto.SignupRequest;
import com.sachin.documind.dto.response.ApiResponse;
import com.sachin.documind.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            String token = authService.login(loginRequest);
            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse(token);
            return ResponseEntity.ok(ApiResponse.success(jwtAuthResponse, "Login successful"));
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest signupRequest){
        try {
            String response = authService.signup(signupRequest);
            return new ResponseEntity<>(ApiResponse.success(response, response), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
