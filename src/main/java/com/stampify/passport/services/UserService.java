package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private AuditPasswordChangeRepository auditPasswordChangeRepository;
    @Autowired private AuditLoginEventRepository auditLoginEventRepository;

    @Autowired private Argon2PasswordEncoder passwordEncoder;
    @Autowired private OtpService otpService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* ======================================================
       CREATE USER
       ====================================================== */
    public User createUser(RegisterUserRequest req, User actorUser) throws Exception {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        Organization organization = getOrganization(req.getOrganizationId());

        User newUser = switch (req.getRole().toUpperCase()) {

            case "ADMIN" -> {
                Admin admin = new Admin();
                mapCommonFields(admin, req, organization);
                yield adminRepository.save(admin);
            }

            case "MEMBER" -> {
                Member member = new Member();
                mapCommonFields(member, req, organization);
                yield memberRepository.save(member);
            }

            case "SCANNER" -> {
                Scanner scanner = new Scanner();
                mapCommonFields(scanner, req, organization);
                yield scannerRepository.save(scanner);
            }

            default -> throw new IllegalArgumentException("Invalid role");
        };

        logAudit(actorUser, organization, "USER", "CREATE", newUser, null, newUser);

        return newUser;
    }

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
       READ USER
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
    public Optional<User> login(String email, String password, String ipAddress, String userAgent) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()));

        User actorUser = userOpt.orElse(null);

        // Audit login
        AuditLog auditLog = createAuditLog(actorUser, null, "USER", "LOGIN", null, null);
        AuditLoginEvent loginEvent = new AuditLoginEvent();
        loginEvent.setAuditLog(auditLog);
        loginEvent.setUser(actorUser);
        loginEvent.setOccurredAt(LocalDateTime.now());
        loginEvent.setSuccessful(userOpt.isPresent());
        if (!userOpt.isPresent()) loginEvent.setFailureReason("Invalid credentials");
        auditLoginEventRepository.save(loginEvent);

        return userOpt;
    }

    /* ======================================================
       UPDATE USER
       ====================================================== */
    public Optional<User> editUser(Long id, User incoming, User actorUser) throws Exception {
        return userRepository.findById(id).map(existing -> {

            if (!existing.getClass().equals(incoming.getClass())) {
                throw new IllegalStateException("Role change is not allowed");
            }

            String previousData = serialize(existing);

            existing.setFirstName(incoming.getFirstName());
            existing.setLastName(incoming.getLastName());
            existing.setEmail(incoming.getEmail());
            existing.setActive(incoming.getActive());
            existing.setOrganization(getOrganization(incoming.getOrganization().getId()));
            existing.setUpdatedAt(LocalDateTime.now());

            User saved = userRepository.save(existing);

            try {
                logAudit(actorUser, existing.getOrganization(), "USER", "UPDATE", saved, previousData, saved);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return saved;
        });
    }

    /* ======================================================
       DELETE USER
       ====================================================== */
    public void deleteUser(Long id, User actorUser) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.softDelete(actorUser != null ? actorUser.getEmail() : "SYSTEM");

        userRepository.save(user);

        logAudit(actorUser, user.getOrganization(), "USER", "DELETE", user, serialize(user), null);
    }

    /* ======================================================
       PASSWORD CHANGE
       ====================================================== */
    public Optional<User> changePassword(String email, String oldPass, String newPass, User actorUser) throws Exception {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {
                    if (!passwordEncoder.matches(oldPass, user.getPasswordHash())) {
                        throw new IllegalArgumentException("Invalid password");
                    }

                    String previousData = serialize(user);

                    user.setPasswordHash(passwordEncoder.encode(newPass));
                    user.setUpdatedAt(LocalDateTime.now());
                    User saved = userRepository.save(user);

                    AuditLog auditLog = null;
                    try {
                        auditLog = createAuditLog(actorUser, user.getOrganization(), "USER", "PASSWORD_CHANGE", null, previousData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    AuditPasswordChange pwChange = new AuditPasswordChange();
                    pwChange.setAuditLog(auditLog);
                    pwChange.setUser(saved);
                    pwChange.setChangedBy(actorUser);
                    pwChange.setChangedAt(LocalDateTime.now());
                    auditPasswordChangeRepository.save(pwChange);

                    return saved;
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

    /* ======================================================
       HELPER METHODS
       ====================================================== */
    private void logAudit(User actorUser, Organization org, String entityName, String actionName,
                          Object newEntity, String previousData, Object newData) throws Exception {

        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(actorUser);
        auditLog.setOrganization(org);
        auditLog.setActionCategory(entityName);
        auditLog.setActionName(actionName);
        if (newEntity instanceof User u) auditLog.setEntityId(u.getId());
        auditLog.setEntityName(entityName);
        auditLog.setPreviousData(previousData);
        auditLog.setNewData(newData != null ? serialize(newData) : null);
        auditLog.setOccurredAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    private AuditLog createAuditLog(User actorUser, Organization org, String entityName, String actionName,
                                    Object newEntity, String previousData) throws Exception {
        AuditLog log = new AuditLog();
        log.setActorUser(actorUser);
        log.setOrganization(org);
        log.setActionCategory(entityName);
        log.setActionName(actionName);
        log.setEntityId(newEntity instanceof User u ? u.getId() : null);
        log.setEntityName(entityName);
        log.setPreviousData(previousData);
        log.setNewData(newEntity != null ? serialize(newEntity) : null);
        log.setOccurredAt(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
