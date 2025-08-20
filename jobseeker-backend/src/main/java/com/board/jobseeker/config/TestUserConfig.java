package com.board.jobseeker.config;

import com.board.jobseeker.auth.User;
import com.board.jobseeker.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestUserConfig implements CommandLineRunner {

    // Inject test user credentials from environment variables
    @Value("${test.user.poster.username:#{null}}")
    private String jobPosterUsername;
    
    @Value("${test.user.poster.password:#{null}}")
    private String jobPosterPassword;
    
    @Value("${test.user.seeker.username:#{null}}")
    private String jobSeekerUsername;
    
    @Value("${test.user.seeker.password:#{null}}")
    private String jobSeekerPassword;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create test users if they don't exist
        if (!userRepository.existsByUsername(jobPosterUsername)) {
            User poster = new User(
                jobPosterUsername,
                passwordEncoder.encode(jobPosterPassword),
                jobPosterUsername + "@test.com",
                User.Role.POST_OWNER
            );
            userRepository.save(poster);
        }

        if (!userRepository.existsByUsername(jobSeekerUsername)) {
            User seeker = new User(
                jobSeekerUsername,
                passwordEncoder.encode(jobSeekerPassword),
                jobSeekerUsername + "@test.com",
                User.Role.JOB_SEEKER
            );
            userRepository.save(seeker);
        }
    }
}