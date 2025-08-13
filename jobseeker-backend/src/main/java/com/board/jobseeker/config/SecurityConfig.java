package com.board.jobseeker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration makes any beans in this class available in auto-config engine 
@Configuration
public class SecurityConfig {

    // Inject test user credentials from environment variables
    @Value("${test.user.poster.username:#{null}}")
    private String jobPosterUsername;
    
    @Value("${test.user.poster.password:#{null}}")
    private String jobPosterPassword;
    
    @Value("${test.user.seeker.username:#{null}}")
    private String jobSeekerUsername;
    
    @Value("${test.user.seeker.password:#{null}}")
    private String jobSeekerPassword;

    // @Bean - expect a bean to config filter chain
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/", "/results.html").permitAll()
                        .anyRequest().authenticated()
                        )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return http.build();
   }

   @Bean
   PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

   // all API tests that make a HTTP request will use this example user to authenticate 
   @Bean 
   @Profile({"dev", "test"})
   UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
    User.UserBuilder users = User.builder(); 
    // jobPoster is a company job poster 
    UserDetails jobPoster = users
        .username(jobPosterUsername)
        .password(passwordEncoder.encode(jobPosterPassword))
        .roles("POST-OWNER")
        .build();

    // jobSeeker is a job seeker, therefore never having posted any job entries nor having the privilege to 
    UserDetails jobSeeker = users
        .username(jobSeekerUsername)
        .password(passwordEncoder.encode(jobSeekerPassword))
        .roles("JOB-SEEKER")
        .build(); 
    return new InMemoryUserDetailsManager(jobPoster, jobSeeker); 
   }

   // when initially setting up the database in production, create master users 
   @Bean 
   @Profile({"docker", "prod"}) 
   UserDetailsService masterUsers() {
        return new InMemoryUserDetailsManager(); 
   }
}
