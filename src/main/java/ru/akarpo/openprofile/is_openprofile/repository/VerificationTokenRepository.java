package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.VerificationToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByTokenAndType(String token, String type);
    void deleteByExpiryDateBefore(Instant date);
}