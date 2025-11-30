package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMediaId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileMediaRepository extends JpaRepository<ProfileMedia, ProfileMediaId> {
    List<ProfileMedia> findByProfileId(UUID profileId);
    List<ProfileMedia> findByMediaId(UUID mediaId);
    Optional<ProfileMedia> findByProfileIdAndMediaId(UUID profileId, UUID mediaId);
}
