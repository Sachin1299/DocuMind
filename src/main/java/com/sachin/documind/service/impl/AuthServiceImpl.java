package com.sachin.documind.service.impl;

import com.sachin.documind.dto.LoginRequest;
import com.sachin.documind.dto.SignupRequest;
import com.sachin.documind.entity.User;
import com.sachin.documind.repository.UserRepository;
import com.sachin.documind.security.JwtTokenProvider;
import com.sachin.documind.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.usernameOrEmail(),
                        loginRequest.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String signup(SignupRequest signupRequest) {
        if(userRepository.existsByUsername(signupRequest.username())){
            throw new RuntimeException("Username already exists!");
        }

        if(userRepository.existsByEmail(signupRequest.email())){
            throw new RuntimeException("Email already exists!");
        }

        User user = new User(
                signupRequest.name(),
                signupRequest.username(),
                signupRequest.email(),
                passwordEncoder.encode(signupRequest.password())
        );

        userRepository.save(user);

        return "User registered successfully!";
    }
}
