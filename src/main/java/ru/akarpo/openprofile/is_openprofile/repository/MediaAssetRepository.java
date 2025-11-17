package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
    List<MediaAsset> findByUserId(UUID userId);
}