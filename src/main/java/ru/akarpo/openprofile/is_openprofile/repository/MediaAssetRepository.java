package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    Optional<MediaAsset> findByUserId(UUID userId);
}
