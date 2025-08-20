package com.board.jobseeker.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        try {
            User user = authService.registerUser(request);
            return ResponseEntity.ok(new AuthDTO.AuthResponse(
                "registration-successful", 
                user.getUsername(), 
                user.getRole().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // TODO: implement proper tokenization 
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        try {
            Authentication auth = authService.authenticateUser(request.getUsername(), request.getPassword());
            User user = (User) auth.getPrincipal();
            
            // create user-specific simple token 
            String token = "login-successful-" + user.getUsername();
            
            return ResponseEntity.ok(new AuthDTO.AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
}