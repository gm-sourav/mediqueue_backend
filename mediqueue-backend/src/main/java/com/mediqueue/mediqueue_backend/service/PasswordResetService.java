package com.mediqueue.mediqueue_backend.service;

import com.mediqueue.mediqueue_backend.entity.PasswordResetToken;
import com.mediqueue.mediqueue_backend.entity.User;
import com.mediqueue.mediqueue_backend.repository.PasswordResetTokenRepository;
import com.mediqueue.mediqueue_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int CODE_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_SECONDS = 60;
    private static final String INVALID = "Invalid or expired code";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    private final SecureRandom random = new SecureRandom();

    /**
     * Always returns normally, even if the email is not registered,
     * so nobody can use this endpoint to find out which emails have accounts.
     */
    @Transactional
    public void requestReset(String email) {
        Optional<User> found = userRepository.findByEmail(email.trim());
        if (found.isEmpty()) return;
        User user = found.get();

        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = tokenRepository.findByUser(user).orElse(null);

        // Throttle: one new code per minute per user
        if (token != null && token.getCreatedAt().plusSeconds(RESEND_SECONDS).isAfter(now)) return;

        if (token == null) {
            token = new PasswordResetToken();
            token.setUser(user);
        }

        String code = String.format("%06d", random.nextInt(1_000_000));
        token.setCodeHash(passwordEncoder.encode(code));
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(CODE_MINUTES));
        token.setAttempts(0);
        tokenRepository.save(token);

        sendCode(user, code);
    }

    /** noRollbackFor: the "wrong attempt" counter must be saved even though we throw. */
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException(INVALID));
        PasswordResetToken token = tokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException(INVALID));

        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getAttempts() >= MAX_ATTEMPTS) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException(INVALID);
        }

        if (!passwordEncoder.matches(code, token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            tokenRepository.save(token);
            throw new IllegalArgumentException(INVALID);
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token); // code can be used only once
    }

    private void sendCode(User user, String code) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            // No SMTP configured (development): print the code in the server console.
            log.warn("[DEV] Password reset code for {}: {}", user.getEmail(), code);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isBlank()) msg.setFrom(fromAddress);
            msg.setTo(user.getEmail());
            msg.setSubject("MediQueue password reset code");
            msg.setText("Hello " + user.getName() + ",\n\n"
                    + "Your MediQueue password reset code is: " + code + "\n"
                    + "It expires in " + CODE_MINUTES + " minutes. "
                    + "If you did not ask for this, you can ignore this email.");
            sender.send(msg);
        } catch (Exception e) {
            log.error("Could not send reset email to {}: {}", user.getEmail(), e.getMessage());
            log.warn("[DEV] Password reset code for {}: {}", user.getEmail(), code);
        }
    }
}