package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findBySlug(String slug);
    Optional<Profile> findByUserId(UUID userId);
}