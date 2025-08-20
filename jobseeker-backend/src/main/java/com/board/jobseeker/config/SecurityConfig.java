package com.board.jobseeker.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @Configuration makes any beans in this class available in auto-config engine 
@Configuration
public class SecurityConfig {

    // @Bean - expect a bean to config filter chain
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // permit all authorization related and homepage, API access (information retrieval/posting should be authorized)
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/", "/results.html").permitAll()
                        .anyRequest().authenticated()
                        )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(customTokenFilter(), UsernamePasswordAuthenticationFilter.class) 
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // TODO: REMOVE CUSTOM TOKEN FILTER LATER, currently only for testing 
    @Bean
    public OncePerRequestFilter customTokenFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    // map tokens to users
                    String username = null;
                    if ("login-successful-miles1".equals(token)) {
                        username = "miles1";
                    } else if ("login-successful-job-searcher".equals(token)) {
                        username = "job-searcher";
                    }
                    
                    if (username != null) {
                        UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(username, null, 
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
