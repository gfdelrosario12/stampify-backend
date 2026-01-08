package com.stampify.passport.controllers;

import com.stampify.passport.config.JwtUtil;
import com.stampify.passport.dto.LoginRequest;
import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.dto.UserDTO;
import com.stampify.passport.mappers.UserMapper;
import com.stampify.passport.models.User;
import com.stampify.passport.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /* ================= REGISTRATION ================= */
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(
            @RequestBody RegisterUserRequest request,
            @RequestAttribute("username") String actorEmail) throws Exception {

        User actorUser = userService.getByEmail(actorEmail)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        User user = userService.createUser(request, actorUser);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestBody LoginRequest request,
            HttpServletRequest req,
            HttpServletResponse res) throws Exception {

        String ipAddress = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");

        Optional<User> userOpt = userService.login(request.getEmail(), request.getPassword(), ipAddress, userAgent);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();

        // Generate JWT with role
        String token = jwtUtil.generateToken(user.getEmail(), user.getClass().getSimpleName());

        // Set HTTP-only cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        res.addCookie(cookie);

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /* ================= LOGOUT ================= */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }

    /* ================= GET LOGGED-IN USER ================= */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(@RequestAttribute("username") String email) {
        return userService.getByEmail(email)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    /* ================= CHANGE PASSWORD ================= */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestAttribute("username") String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            Optional<User> userOpt = userService.changePassword(email, oldPassword, newPassword,
                    userService.getByEmail(email).orElse(null));

            if (userOpt.isPresent()) {
                return ResponseEntity.ok("Password changed successfully.");
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /* ================= READ-ONLY USER VIEW ================= */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getById(id)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ================= ADMIN-ONLY CRUD ================= */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> editUser(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            @RequestAttribute("username") String actorEmail) throws Exception {

        User actorUser = userService.getByEmail(actorEmail)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        return userService.editUser(id, updatedUser, actorUser)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestAttribute("username") String actorEmail) throws Exception {

        User actorUser = userService.getByEmail(actorEmail)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        userService.deleteUser(id, actorUser);
        return ResponseEntity.noContent().build();
    }
}
