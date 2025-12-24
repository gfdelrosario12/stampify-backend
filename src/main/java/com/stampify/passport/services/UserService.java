package com.stampify.passport.services;

import com.stampify.passport.models.User;
import com.stampify.passport.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService; // use the separate OTP service

    /* ================= CREATE / REGISTRATION ================= */
    public User createUser(User user) {
        user.setEmail(user.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    /* ================= READ ================= */
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* ================= UPDATE USER INFO ================= */
    public Optional<User> editUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    existingUser.setActive(updatedUser.getActive());
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                });
    }

    /* ================= DELETE ================= */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /* ================= LOGIN ================= */
    public Optional<User> login(String email, String rawPassword) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(user -> user.getActive() &&
                        passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    /* ================= CHANGE PASSWORD ================= */
    public Optional<User> changePassword(String email, String oldPassword, String newPassword) {
        return userRepository.findByEmail(email.toLowerCase().trim())
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

    /* ================= GENERATE OTP AND SEND EMAIL ================= */
    public String generateAndSendOtp(String email) {
        String otp = otpService.generateOtp(email);

        sendOtpEmail(email, otp);

        return otp;
    }

    private void sendOtpEmail(String email, String otp) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.example.com"); // placeholder SMTP
            mailSender.setPort(587);
            mailSender.setUsername("your_email@example.com"); // placeholder
            mailSender.setPassword("your_password"); // placeholder

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("your_email@example.com");
            message.setSubject("Your OTP Code");
            message.setText("Your OTP is: " + otp + "\nIt expires in 5 minutes.");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    /* ================= EMERGENCY PASSWORD CHANGE ================= */
    public Optional<User> emergencyPasswordChange(String email, String otp, String newPassword) {
        if (!otpService.verifyOtp(email, otp)) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }

    /* ================= EMERGENCY EMAIL CHANGE ================= */
    public Optional<User> emergencyEmailChange(String oldEmail, String otp, String newEmail) {
        if (!otpService.verifyOtp(oldEmail, otp)) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }

        if (userRepository.findByEmail(newEmail.toLowerCase().trim()).isPresent()) {
            throw new IllegalArgumentException("New email already in use.");
        }

        return userRepository.findByEmail(oldEmail.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {
                    user.setEmail(newEmail.toLowerCase().trim());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }
}
