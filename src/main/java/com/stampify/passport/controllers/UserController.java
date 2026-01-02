package com.stampify.passport.controllers;

import com.stampify.passport.dto.LoginRequest;
import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.models.User;
import com.stampify.passport.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /* ================= REGISTRATION ================= */
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword())
                .map(ResponseEntity::ok)
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
                    .map(user -> ResponseEntity.ok("Password changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
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
                    .map(user -> ResponseEntity.ok("Emergency password changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
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
                    .map(user -> ResponseEntity.ok("Email changed successfully."))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    /* ================= CRUD ================= */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editUser(@PathVariable Long id, @RequestBody User user) {
        return userService.editUser(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
