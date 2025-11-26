package com.syllabus.copilot.backend.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.syllabus.copilot.backend.model.User;
import com.syllabus.copilot.backend.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepo;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String email, String password) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        User found = user.get();
        // In production, compare hashed passwords using bcrypt
        if (!found.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return found;
    }

    public User signup(String name, String email, String password) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password); // In production, hash with bcrypt
        
        return userRepo.save(newUser);
    }
}
