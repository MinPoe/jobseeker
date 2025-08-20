package com.board.jobseeker.util;

import com.board.jobseeker.auth.AuthDTO;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

@Component
public class AuthHelpers {
    
    public String getAuthToken(TestRestTemplate restTemplate, String username, String password) {
        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        
        ResponseEntity<AuthDTO.AuthResponse> response = restTemplate.postForEntity(
            "/api/auth/login", 
            loginRequest, 
            AuthDTO.AuthResponse.class
        );
        
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getToken();
        }
        return null;
    }
    
    // craft HTTP header with JWT token 
    public HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return headers;
    }
    
    // for requests with auth header, no request body (GET, DELETE)
    public HttpEntity<Void> createAuthEntity(String token) {
        return new HttpEntity<>(createAuthHeaders(token));
    }
    
    // auth header AND request body (POST, PUT)
    public <T> HttpEntity<T> createAuthEntity(T body, String token) {
        return new HttpEntity<>(body, createAuthHeaders(token));
    }
    
    // auth header AND specific content (PATCH, json-patch+json)
    public HttpHeaders createAuthHeadersWithContentType(String token, MediaType contentType) {
        HttpHeaders headers = createAuthHeaders(token);
        headers.setContentType(contentType);
        return headers;
    }
}