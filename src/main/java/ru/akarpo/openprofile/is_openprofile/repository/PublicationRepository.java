package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.Publication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, UUID> {
    List<Publication> findByProfileId(UUID profileId);
    Optional<Publication> findByProfileIdAndActiveTrue(UUID profileId);
    List<Publication> findByProfileIdOrderByVersionDesc(UUID profileId);
}