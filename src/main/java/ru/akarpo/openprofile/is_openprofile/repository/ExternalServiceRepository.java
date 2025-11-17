package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.ExternalService;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExternalServiceRepository extends JpaRepository<ExternalService, UUID> {
    Optional<ExternalService> findByCode(String name);
}