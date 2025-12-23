package com.icpepsencr.passport.services;

import com.icpepsencr.passport.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    User createUser(User user);
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);
}
