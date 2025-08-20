package com.board.jobseeker.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTO {
    
    public static class LoginRequest {
        @NotBlank
        private String username;
        
        @NotBlank
        private String password;
        
        public LoginRequest() {}
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    // TODO: Decide whether or not to allow users to be able to register as a job poster? How to avoid scams? 
    public static class RegisterRequest {
        @NotBlank
        @Size(min = 5, max = 20)
        private String username;
        
        @NotBlank
        @Size(min = 6, max = 40)
        private String password;
        
        @NotBlank
        @Email
        private String email;
        
        @NotBlank
        private String role; // "JOB_SEEKER" or "POST_OWNER"
        
        public RegisterRequest() {}
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    public static class AuthResponse {
        private String token;
        private String username;
        private String role;
        
        public AuthResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }
        
        public String getToken() { return token; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }
}