package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.PublicProfileView;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicProfileViewRepository extends JpaRepository<PublicProfileView, UUID> {
    Optional<PublicProfileView> findByPublicationId(UUID publicationId);
    Optional<PublicProfileView> findBySlug(String slug);
    Optional<PublicProfileView> findByProfileId(UUID profileId);
}