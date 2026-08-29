package com.cloudstorage.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloudstorage.dto.request.LoginRequest;
import com.cloudstorage.dto.request.RegisterRequest;
import com.cloudstorage.dto.response.LoginResponse;
import com.cloudstorage.entity.User;
import com.cloudstorage.repository.UserRepository;
import com.cloudstorage.security.JwtService;

import com.cloudstorage.exception.InvalidCredentialsException;
import com.cloudstorage.exception.ResourceAlreadyExistsException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
        userRepository.save(user);
        return "User registered successfully";
    }
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())) {                               
            throw new RuntimeException("Invalid email or password" );                               
        }
        String token = jwtService.generateToken(user);
        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}