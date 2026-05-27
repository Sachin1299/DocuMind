package com.sachin.documind.service;

import com.sachin.documind.dto.LoginRequest;
import com.sachin.documind.dto.SignupRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
    String signup(SignupRequest signupRequest);
}
