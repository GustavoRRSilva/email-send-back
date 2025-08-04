package com.engeman.technical_test_engeman.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.engeman.technical_test_engeman.domain.User;
import com.engeman.technical_test_engeman.emailService.dto.EmailServiceSMTPdto;
import com.engeman.technical_test_engeman.loginauth.dto.LoginRequestDTO;
import com.engeman.technical_test_engeman.loginauth.dto.RegisterDTO;
import com.engeman.technical_test_engeman.loginauth.dto.ResetPasswordDTO;
import com.engeman.technical_test_engeman.loginauth.dto.ResponseDTO;
import com.engeman.technical_test_engeman.repositories.UserRepository;
import com.engeman.technical_test_engeman.security.infra.TokenService;
import com.engeman.technical_test_engeman.services.EmailServiceSMTP;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceSMTP emailServiceSMTP;
    private final TokenService tokenService;

    public AuthController(UserRepository repository, PasswordEncoder passwordEncoder, TokenService tokenService,EmailServiceSMTP emailServiceSMTP) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
		this.emailServiceSMTP = emailServiceSMTP;
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
    
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO body) {
        String token = body.token();
        String newPassword = body.newPassword();

        User user = repository.findByResetPasswordToken(token)
            .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));

       
        user.setPassword(passwordEncoder.encode(newPassword));
        
        user.setResetPasswordToken(null);


        repository.save(user);

        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String,String> body){
    	String email = body.get("email");
        Optional<User> userOpt = repository.findByEmail(email);
        if(userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email não encontrado");
        }
        
        User user = userOpt.get();
        
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        repository.save(user); 
        String resetLink ="http://localhost:3000/auth/reset-password?token=" + resetToken;
        emailServiceSMTP.SendSimpleMessage(new EmailServiceSMTPdto(user.getEmail(),"Recuperação de senha","Clique no link para resetar sua senha: " + resetLink));
        return ResponseEntity.ok(Map.of("status", "Sucesso"));

    }
}
