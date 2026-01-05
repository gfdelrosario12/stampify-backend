package com.stampify.passport.services;

import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.models.*;
import com.stampify.passport.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
@Transactional
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ScannerRepository scannerRepository;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private PassportRepository passportRepository;

    @Autowired private Argon2PasswordEncoder passwordEncoder;
    @Autowired private OtpService otpService;

    /* ======================================================
       CREATE USER (ROLE AWARE, CLEAN)
       ====================================================== */

    public User createUser(RegisterUserRequest req) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        Organization organization = getOrganization(req.getOrganizationId());

        return switch (req.getRole().toUpperCase()) {

            case "ADMIN" -> {
                Admin admin = new Admin();
                mapCommonFields(admin, req, organization);
                yield adminRepository.save(admin);
            }

            case "MEMBER" -> {
                Member member = new Member();
                mapCommonFields(member, req, organization);

                Member savedMember = memberRepository.save(member);

                /* Auto-create initial passport */
                Passport passport = new Passport();
                passport.setMember(savedMember);
                passport.setIssuedAt(LocalDateTime.now());
                passport.setExpiresAt(LocalDateTime.now().plusYears(1));
                passport.setPassportStatus("ACTIVE");
                passport.setCreatedAt(LocalDateTime.now());

                passportRepository.save(passport);

                yield savedMember;
            }

            case "SCANNER" -> {
                Scanner scanner = new Scanner();
                mapCommonFields(scanner, req, organization);
                yield scannerRepository.save(scanner);
            }

            default -> throw new IllegalArgumentException("Invalid role");
        };
    }

    /* ======================================================
       COMMON FIELD MAPPER (USER LEVEL)
       ====================================================== */

    private void mapCommonFields(User user, RegisterUserRequest req, Organization organization) {
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setOrganization(organization);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
    }

    private Organization getOrganization(Long orgId) {
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid organization"));
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
       UPDATE (ROLE SAFE, SIMPLE)
       ====================================================== */

    public Optional<User> editUser(Long id, User incoming) {

        return userRepository.findById(id).map(existing -> {

            /* ===== USER FIELDS ===== */
            existing.setFirstName(incoming.getFirstName());
            existing.setLastName(incoming.getLastName());
            existing.setEmail(incoming.getEmail());
            existing.setActive(incoming.getActive());
            existing.setOrganization(
                    getOrganization(incoming.getOrganization().getId())
            );
            existing.setUpdatedAt(LocalDateTime.now());

            /* ===== ROLE CHECK (NO MUTATION) ===== */
            if (!existing.getClass().equals(incoming.getClass())) {
                throw new IllegalStateException("Role change is not allowed");
            }

            return userRepository.save(existing);
        });
    }

    /* ======================================================
       DELETE (CASCADE SAFE)
       ====================================================== */

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        /*
         * ENTITY CASCADES:
         * - MEMBER  → passports deleted automatically
         * - SCANNER → stamps deleted automatically
         * - ADMIN   → no dependents
         */
        userRepository.delete(user);
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

        return userRepository.findByEmail(email.toLowerCase().trim())
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

        return userRepository.findByEmail(oldEmail.toLowerCase().trim())
                .map(user -> {
                    user.setEmail(newEmail.toLowerCase().trim());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }
}
