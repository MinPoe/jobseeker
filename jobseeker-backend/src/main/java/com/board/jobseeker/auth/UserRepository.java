package com.board.jobseeker.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    
    // functions for registration to ensure unique username and email
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}