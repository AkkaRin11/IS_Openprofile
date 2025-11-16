package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.PublicProfileView;
import java.util.UUID;

@Repository
public interface PublicProfileViewRepository extends JpaRepository<PublicProfileView, UUID> {
}