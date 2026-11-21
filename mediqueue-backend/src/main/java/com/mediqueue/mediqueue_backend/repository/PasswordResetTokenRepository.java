package com.mediqueue.mediqueue_backend.repository;

import com.mediqueue.mediqueue_backend.entity.PasswordResetToken;
import com.mediqueue.mediqueue_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
}
