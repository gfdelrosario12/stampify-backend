package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.User;
import com.icpepsencr.passport.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    /* ================= CREATE / REGISTRATION ================= */
    public User createUser(User user) {
        user.setUsername(user.getUsername().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    /* ================= READ ================= */
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase().trim());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* ================= UPDATE USER INFO ================= */
    public Optional<User> editUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Update editable fields only (exclude username/email and password)
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setActive(updatedUser.getActive());

                    // Add audit timestamp
                    existingUser.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(existingUser);
                });
    }

    /* ================= DELETE ================= */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /* ================= LOGIN ================= */
    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username.toLowerCase().trim())
                .filter(user ->
                        user.getActive() &&
                                passwordEncoder.matches(rawPassword, user.getPasswordHash())
                );
    }

    /* ================= CHANGE PASSWORD ================= */
    public Optional<User> changePassword(String username, String oldPassword, String newPassword) {
        return userRepository.findByUsername(username.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {
                    if (oldPassword != null && !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                        throw new IllegalArgumentException("Old password does not match.");
                    }
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }
}
