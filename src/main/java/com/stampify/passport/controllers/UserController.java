package com.stampify.passport.controllers;

import com.stampify.passport.dto.*;
import com.stampify.passport.mappers.UserMapper;
import com.stampify.passport.models.User;
import com.stampify.passport.repositories.UserRepository;
import com.stampify.passport.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /* ================= REGISTRATION ================= */
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(
            @RequestBody RegisterUserRequest request) {

        User user = userService.createUser(request);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestBody LoginRequest request) {

        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user)))
                .orElse(ResponseEntity.status(401).build());
    }

    /* ================= CHANGE PASSWORD ================= */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            return userService.changePassword(email, oldPassword, newPassword)
                    .map(u -> ResponseEntity.ok("Password changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ================= EMERGENCY PASSWORD CHANGE ================= */
    @PostMapping("/emergency-password")
    public ResponseEntity<String> emergencyPasswordChange(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {

        try {
            return userService.emergencyPasswordChange(email, otp, newPassword)
                    .map(u -> ResponseEntity.ok("Emergency password changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ================= EMERGENCY EMAIL CHANGE ================= */
    @PostMapping("/emergency-email")
    public ResponseEntity<String> emergencyEmailChange(
            @RequestParam String oldEmail,
            @RequestParam String otp,
            @RequestParam String newEmail) {

        try {
            return userService.emergencyEmailChange(oldEmail, otp, newEmail)
                    .map(u -> ResponseEntity.ok("Email changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /* ================= READ-ONLY USER VIEW ================= */

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ================= ADMIN-ONLY CRUD ================= */

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> editUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        return userService.editUser(id, updatedUser)
                .map(user -> ResponseEntity.ok(UserMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
