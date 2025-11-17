package ru.akarpo.openprofile.is_openprofile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceConnectionRepository extends JpaRepository<ServiceConnection, UUID> {
    List<ServiceConnection> findByUserId(UUID userId);
    List<ServiceConnection> findByServiceId(UUID serviceId);
    Optional<ServiceConnection> findByUserIdAndServiceIdAndExternalUserId(
        UUID userId, UUID serviceId, String externalUserId);
}