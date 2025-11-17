package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.Theme;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, UUID> {
    Optional<Theme> findByName(final String name);
}
