package ru.akarpo.openprofile.is_openprofile.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import java.util.UUID;

@Repository
public interface ProfileWidgetRepository extends JpaRepository<ProfileWidget, UUID> {
}