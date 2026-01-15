package com.stampify.passport.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stampify.passport.dto.RegisterUserRequest;
import com.stampify.passport.dto.UserDTO;
import com.stampify.passport.mappers.UserMapper;
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
    public User createUser(RegisterUserRequest req, User actorUser) {

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        Organization organization = getOrCreateOrganizationFromEmail(req.getEmail());

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
                OrgScanner scanner = new OrgScanner();
                mapCommonFields(scanner, req, organization);
                yield scannerRepository.save(scanner);
            }

            default -> throw new IllegalArgumentException("Invalid role");
        };

        UserDTO dto = UserMapper.toDTO(newUser);

        logAudit(
                actorUser,
                organization,
                "USER",
                "CREATE",
                newUser,
                null,
                serialize(dto)
        );

        return newUser;
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
    public Optional<User> login(String email, String password, String ipAddress, String userAgent) {

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()));

        User actorUser = userOpt.orElse(null);

        AuditLog auditLog = createAuditLog(
                actorUser,
                null,
                "USER",
                "LOGIN",
                null,
                null,
                null
        );

        AuditLoginEvent event = new AuditLoginEvent();
        event.setAuditLog(auditLog);
        event.setUser(actorUser);
        event.setOccurredAt(LocalDateTime.now());
        event.setSuccessful(userOpt.isPresent());

        if (userOpt.isEmpty()) {
            event.setFailureReason("Invalid credentials");
        }

        auditLoginEventRepository.save(event);

        return userOpt;
    }

    /* ======================================================
       UPDATE USER
       ====================================================== */
    public Optional<User> editUser(Long id, User incoming, User actorUser) {

        return userRepository.findById(id).map(existing -> {

            if (!existing.getClass().equals(incoming.getClass())) {
                throw new IllegalStateException("Role change is not allowed");
            }

            UserDTO before = UserMapper.toDTO(existing);

            existing.setFirstName(incoming.getFirstName());
            existing.setLastName(incoming.getLastName());
            existing.setEmail(incoming.getEmail());
            existing.setActive(incoming.getActive());
            existing.setOrganization(getOrganization(incoming.getOrganization().getId()));
            existing.setUpdatedAt(LocalDateTime.now());

            User saved = userRepository.save(existing);

            UserDTO after = UserMapper.toDTO(saved);

            logAudit(
                    actorUser,
                    saved.getOrganization(),
                    "USER",
                    "UPDATE",
                    saved,
                    serialize(before),
                    serialize(after)
            );

            return saved;
        });
    }

    /* ======================================================
       DELETE USER
       ====================================================== */
    public void deleteUser(Long id, User actorUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDTO before = UserMapper.toDTO(user);

        user.softDelete(actorUser != null ? actorUser.getEmail() : "SYSTEM");
        userRepository.save(user);

        logAudit(
                actorUser,
                user.getOrganization(),
                "USER",
                "DELETE",
                user,
                serialize(before),
                null
        );
    }

    /* ======================================================
       PASSWORD CHANGE
       ====================================================== */
    public Optional<User> changePassword(String email, String oldPass, String newPass, User actorUser) {

        return userRepository.findByEmail(email.toLowerCase().trim())
                .filter(User::getActive)
                .map(user -> {

                    if (!passwordEncoder.matches(oldPass, user.getPasswordHash())) {
                        throw new IllegalArgumentException("Invalid password");
                    }

                    UserDTO before = UserMapper.toDTO(user);

                    user.setPasswordHash(passwordEncoder.encode(newPass));
                    user.setUpdatedAt(LocalDateTime.now());
                    User saved = userRepository.save(user);

                    UserDTO after = UserMapper.toDTO(saved);

                    AuditLog auditLog = createAuditLog(
                            actorUser,
                            user.getOrganization(),
                            "USER",
                            "PASSWORD_CHANGE",
                            saved,
                            serialize(before),
                            serialize(after)
                    );

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
       OTP
       ====================================================== */
    public String generateAndSendOtp(String email) {
        String otp = otpService.generateOtp(email);
        sendOtpEmail(email, otp);
        return otp;
    }

    /* ======================================================
       INTERNAL HELPERS
       ====================================================== */
    private Organization getOrCreateOrganizationFromEmail(String email) {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase().trim();

        return organizationRepository.findByDomain(domain)
                .orElseGet(() -> {
                    Organization org = new Organization();
                    org.setName(domain.split("\\.")[0]);
                    org.setDomain(domain);
                    return organizationRepository.save(org);
                });
    }

    private Organization getOrganization(Long orgId) {
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid organization"));
    }

    private void mapCommonFields(User user, RegisterUserRequest req, Organization org) {
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setOrganization(org);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
    }

    private void logAudit(
            User actorUser,
            Organization org,
            String entity,
            String action,
            Object refEntity,
            String previousData,
            String newData
    ) {
        AuditLog log = new AuditLog();
        log.setActorUser(actorUser);
        log.setOrganization(org);
        log.setActionCategory(entity);
        log.setActionName(action);
        log.setEntityId(refEntity instanceof User u ? u.getId() : null);
        log.setEntityName(entity);
        log.setPreviousData(previousData);
        log.setNewData(newData);
        log.setOccurredAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }

    private AuditLog createAuditLog(
            User actorUser,
            Organization org,
            String entity,
            String action,
            Object refEntity,
            String previousData,
            String newData
    ) {
        AuditLog log = new AuditLog();
        log.setActorUser(actorUser);
        log.setOrganization(org);
        log.setActionCategory(entity);
        log.setActionName(action);
        log.setEntityId(refEntity instanceof User u ? u.getId() : null);
        log.setEntityName(entity);
        log.setPreviousData(previousData);
        log.setNewData(newData);
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

    private void sendOtpEmail(String email, String otp) {
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("smtp.example.com");
            sender.setPort(587);
            sender.setUsername("your_email@example.com");
            sender.setPassword("password");

            Properties props = sender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Your OTP Code");
            msg.setText("Your OTP is: " + otp);

            sender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}