package com.roadresale.service;

import com.roadresale.model.User;
import com.roadresale.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// Rules for registering and logging in.
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String phone, String password) {
        email = email.trim().toLowerCase();

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter your name.");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Please enter a valid email.");
        }
        if (!phone.trim().matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must be 10 digits.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("This email is already registered.");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email);
        user.setPhone(phone.trim());
        // Store a scrambled (hashed) password, never the real one.
        user.setPasswordHash(encoder.encode(password));
        userRepository.save(user);
    }

    public User getUser(int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("This user does not exist.");
        }
        return user;
    }

    // Returns the user if email + password are correct, otherwise null.
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            return null;
        }
        if (!encoder.matches(password, user.getPasswordHash())) {
            return null;
        }
        return user;
    }
}
