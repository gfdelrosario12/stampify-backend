package com.icpepsencr.passport.controllers;

import com.icpepsencr.passport.dto.LoginRequest;
import com.icpepsencr.passport.models.User;
import com.icpepsencr.passport.services.UserService;
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
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (userService.getByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok(userService.createUser(user));
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    /* ================= CHANGE PASSWORD ================= */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            return userService.changePassword(username, oldPassword, newPassword)
                    .map(user -> ResponseEntity.ok("Password changed successfully."))
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

    /* ================= EDIT USER INFO ================= */
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
