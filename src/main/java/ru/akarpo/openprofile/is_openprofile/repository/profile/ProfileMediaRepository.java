package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMediaId;

@Repository
public interface ProfileMediaRepository extends JpaRepository<ProfileMedia, ProfileMediaId> {
}