package com.stampify.passport.services;

import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ScannerRepository scannerRepository;
    @Autowired private OrganizationRepository organizationRepository;

    @Autowired private Argon2PasswordEncoder passwordEncoder;
    @Autowired private OtpService otpService;

    /* ======================================================
       CREATE USER (SINGLE ENDPOINT – ROLE AWARE)
       ====================================================== */

    public User createUser(RegisterUserRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        return switch (req.getRole().toUpperCase()) {

            case "ADMIN" -> {
                Admin admin = new Admin();
                mapCommonFields(admin, req);
                admin.setOrganization(
                        organizationRepository.findById(req.getOrganizationId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid organization"))
                );
                yield adminRepository.save(admin);
            }

            case "MEMBER" -> {
                Member member = new Member();
                mapCommonFields(member, req);
                member.setMembershipNumber(req.getMembershipNumber());
                member.setOrganization(
                        organizationRepository.findById(req.getOrganizationId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid organization"))
                );
                member.setJoinedAt(LocalDateTime.now());
                yield memberRepository.save(member);
            }

            case "SCANNER" -> {
                Scanner scanner = new Scanner();
                mapCommonFields(scanner, req);
                scanner.setDeviceIdentifier(req.getDeviceIdentifier());
                scanner.setOrganization(
                        organizationRepository.findById(req.getOrganizationId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid organization"))
                );
                scanner.setRegisteredAt(LocalDateTime.now());
                yield scannerRepository.save(scanner);
            }

            default -> throw new IllegalArgumentException("Invalid role");
        };
    }

    /* ======================================================
       COMMON FIELD MAPPER
       ====================================================== */
    private void mapCommonFields(User user, RegisterUserRequest req) {
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
    }

    /* ======================================================
       READ
       ====================================================== */

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* ======================================================
       LOGIN
       ====================================================== */

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()));
    }

    /* ======================================================
       UPDATE
       ====================================================== */

    public Optional<User> editUser(Long id, User updated) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(updated.getFirstName());
            user.setLastName(updated.getLastName());
            user.setActive(updated.getActive());
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    /* ======================================================
       DELETE
       ====================================================== */

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /* ======================================================
       PASSWORD CHANGE
       ====================================================== */

    public Optional<User> changePassword(String email, String oldPass, String newPass) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {
                    if (!passwordEncoder.matches(oldPass, user.getPasswordHash())) {
                        throw new IllegalArgumentException("Invalid password");
                    }
                    user.setPasswordHash(passwordEncoder.encode(newPass));
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }

    /* ======================================================
       OTP FLOW
       ====================================================== */

    public String generateAndSendOtp(String email) {
        String otp = otpService.generateOtp(email);
        sendOtpEmail(email, otp);
        return otp;
    }

    private void sendOtpEmail(String email, String otp) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.example.com");
            mailSender.setPort(587);
            mailSender.setUsername("your_email@example.com");
            mailSender.setPassword("password");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Your OTP Code");
            msg.setText("Your OTP is: " + otp);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public Optional<User> emergencyPasswordChange(String email, String otp, String newPassword) {
        if (!otpService.verifyOtp(email, otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        return userRepository.findByEmail(email.toLowerCase())
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }

    public Optional<User> emergencyEmailChange(String oldEmail, String otp, String newEmail) {
        if (!otpService.verifyOtp(oldEmail, otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        return userRepository.findByEmail(oldEmail)
                .map(user -> {
                    user.setEmail(newEmail.toLowerCase());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }
}
