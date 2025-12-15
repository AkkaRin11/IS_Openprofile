package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findBySlug(String slug);

    List<Profile> findByUserId(UUID userId);

    boolean existsBySlug(String slug);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Profile p WHERE p.id = :id")
    Optional<Profile> findByIdLocked(UUID id);
}