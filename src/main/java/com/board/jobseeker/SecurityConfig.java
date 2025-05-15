package com.board.jobseeker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    // @Bean - expect a bean to config filter chain
    // TODO: enable CSRF when building web page 
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/jobseeker/**")
                        .authenticated())
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
   UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
    User.UserBuilder users = User.builder(); 
    // miles1 is a company job poster 
    UserDetails miles = users
        .username("miles1")
        .password(passwordEncoder.encode("password123"))
        .roles("POST-OWNER")
        .build();

    // nonJobPoster is a job seeker, therefore never having posted any job entries 
    UserDetails nonJobPoster = users
        .username("job-searcher")
        .password(passwordEncoder.encode("no-jobs-posted"))
        .roles("JOB-SEEKER")
        .build(); 
    return new InMemoryUserDetailsManager(miles, nonJobPoster); 
   }
}
