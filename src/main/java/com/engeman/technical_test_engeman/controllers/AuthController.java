package com.engeman.technical_test_engeman.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.engeman.technical_test_engeman.domain.User;
import com.engeman.technical_test_engeman.loginauth.dto.LoginRequestDTO;
import com.engeman.technical_test_engeman.loginauth.dto.RegisterDTO;
import com.engeman.technical_test_engeman.loginauth.dto.ResponseDTO;
import com.engeman.technical_test_engeman.repositories.UserRepository;
import com.engeman.technical_test_engeman.security.infra.TokenService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    public AuthController(UserRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        User user = this.repository.findByEmail(body.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(body.email());
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setName(body.name());

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }

        return ResponseEntity.badRequest().body("Email already registered");
    }
}
