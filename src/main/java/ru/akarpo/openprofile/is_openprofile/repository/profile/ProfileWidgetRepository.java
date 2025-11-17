package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileWidgetRepository extends JpaRepository<ProfileWidget, UUID> {
    List<ProfileWidget> findByProfileId(UUID profileId);
    List<ProfileWidget> findByProfileIdOrderByPositionAsc(UUID profileId);
}