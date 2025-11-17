package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceConnectionRepository extends JpaRepository<ServiceConnection, UUID> {
    Optional<ServiceConnection> findByUserId(UUID userId);
    Optional<ServiceConnection> findByServiceId(UUID serviceId);
    Optional<ServiceConnection> findByUserIdAndServiceIdAndExternalUserId(UUID userId, UUID serviceId, String externalUserId);
}