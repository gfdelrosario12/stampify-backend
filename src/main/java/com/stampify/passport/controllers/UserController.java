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

    /* ================= PUBLIC REGISTRATION ================= */
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(
            @RequestBody RegisterUserRequest request) throws Exception {

        User user = userService.createUser(request, null); // 👈 actor is null
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    /* ================= LOGIN ================= */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @RequestBody LoginRequest request,
            HttpServletRequest req,
            HttpServletResponse res) throws Exception {

        Optional<User> userOpt = userService.login(
                request.getEmail(),
                request.getPassword(),
                req.getRemoteAddr(),
                req.getHeader("User-Agent")
        );

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getClass().getSimpleName());

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        cookie.setAttribute("SameSite", "Lax"); // or None + Secure

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
    public ResponseEntity<UserDTO> getMe(
            @RequestAttribute("username") String email,
            @RequestAttribute("role") String role
    ) {
        return userService.getByEmail(email)
                .map(UserMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
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
