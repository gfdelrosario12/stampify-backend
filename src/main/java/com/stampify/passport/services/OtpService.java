package com.stampify.passport.services;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpCache = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final int OTP_LENGTH = 6;
    private final int EXPIRATION_MINUTES = 5;

    /* ================= GENERATE OTP ================= */
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        OtpEntry entry = new OtpEntry(otp, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        otpCache.put(email.toLowerCase().trim(), entry);

        // Send OTP via email
        sendOtpEmail(email, otp);

        return otp;
    }

    /* ================= VERIFY OTP ================= */
    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpCache.get(email.toLowerCase().trim());
        if (entry == null) return false;

        boolean isValid = entry.getOtp().equals(otp) && LocalDateTime.now().isBefore(entry.getExpiresAt());

        // Remove OTP after successful verification
        if (isValid) {
            otpCache.remove(email.toLowerCase().trim());
        }

        return isValid;
    }

    /* ================= SEND OTP EMAIL ================= */
    private void sendOtpEmail(String email, String otp) {
        try {
            // Pull Gmail credentials from environment variables
            String smtpUser = System.getenv("GMAIL_USERNAME");
            String smtpPass = System.getenv("GMAIL_APP_PASSWORD");

            if (smtpUser == null || smtpPass == null) {
                throw new IllegalStateException("Gmail credentials not set in environment variables.");
            }

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(smtpUser);
            mailSender.setPassword(smtpPass);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "false");

            // HTML content for OTP
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "  .container { font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9; border-radius: 10px; max-width: 500px; margin: auto; }" +
                    "  .header { font-size: 24px; font-weight: bold; color: #333; margin-bottom: 20px; text-align: center; }" +
                    "  .otp { font-size: 32px; font-weight: bold; color: #2E86DE; text-align: center; margin: 20px 0; }" +
                    "  .footer { font-size: 14px; color: #777; text-align: center; margin-top: 20px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'>Your OTP Code</div>" +
                    "    <p>Use the following OTP to complete your action. It is valid for <strong>" + EXPIRATION_MINUTES + " minutes</strong>.</p>" +
                    "    <div class='otp'>" + otp + "</div>" +
                    "    <p class='footer'>If you did not request this, please ignore this email.</p>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            // Send email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setFrom(smtpUser);
            helper.setSubject("Your OTP Code");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            System.out.println("DEBUG: OTP sent to " + email + ": " + otp);

        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    /* ================= OTP ENTRY ================= */
    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime expiresAt;

        public OtpEntry(String otp, LocalDateTime expiresAt) {
            this.otp = otp;
            this.expiresAt = expiresAt;
        }

        public String getOtp() { return otp; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
    }
}
