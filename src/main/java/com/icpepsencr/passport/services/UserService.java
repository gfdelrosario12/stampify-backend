package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public User createUser(User user) {
        return null;
    }

    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    public Optional<User> getByUsername(String username) {
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return List.of();
    }

    public User updateUser(User user) {
        return null;
    }

    public void deleteUser(Long id) {
    }

    public Optional<User> login(String username, String rawPassword) {
        return Optional.empty();
    }
}
