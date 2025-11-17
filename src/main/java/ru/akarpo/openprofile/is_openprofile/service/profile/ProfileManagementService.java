package ru.akarpo.openprofile.is_openprofile.service.profile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileManagementService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public UUID addWidget(UUID profileId, String widgetCode, String title,
                          String settingsJson, String layoutJson) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT add_widget(:profileId, :widgetCode, :title, " +
                "CAST(:settings AS jsonb), CAST(:layout AS jsonb))")
                .setParameter("profileId", profileId)
                .setParameter("widgetCode", widgetCode)
                .setParameter("title", title)
                .setParameter("settings", settingsJson)
                .setParameter("layout", layoutJson)
                .getSingleResult();

            return UUID.fromString(result.toString());
        } catch (Exception e) {
            throw new BadRequestException("Failed to add widget: " + e.getMessage());
        }
    }

    @Transactional
    public UUID connectService(UUID userId, String serviceCode, String externalUserId,
                              String accessToken, String refreshToken, String tokenExpiresAt) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT connect_service(:userId, :serviceCode, :externalUserId, " +
                ":accessToken, :refreshToken, CAST(:tokenExpiresAt AS timestamptz))")
                .setParameter("userId", userId)
                .setParameter("serviceCode", serviceCode)
                .setParameter("externalUserId", externalUserId)
                .setParameter("accessToken", accessToken)
                .setParameter("refreshToken", refreshToken)
                .setParameter("tokenExpiresAt", tokenExpiresAt)
                .getSingleResult();

            return UUID.fromString(result.toString());
        } catch (Exception e) {
            throw new BadRequestException("Failed to connect service: " + e.getMessage());
        }
    }

    @Transactional
    public void bindWidgetToConnection(UUID widgetId, UUID connectionId) {
        try {
            entityManager.createNativeQuery(
                "SELECT bind_widget_to_connection(:widgetId, :connectionId)")
                .setParameter("widgetId", widgetId)
                .setParameter("connectionId", connectionId)
                .getSingleResult();
        } catch (Exception e) {
            throw new BadRequestException("Failed to bind widget: " + e.getMessage());
        }
    }

    @Transactional
    public String publishProfile(UUID profileId) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT publish_profile(:profileId)")
                .setParameter("profileId", profileId)
                .getSingleResult();

            return result.toString();
        } catch (Exception e) {
            throw new BadRequestException("Failed to publish profile: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String getPublicProfile(String slug) {
        try {
            Object result = entityManager.createNativeQuery(
                "SELECT get_public_profile(:slug)")
                .setParameter("slug", slug)
                .getSingleResult();

            if (result == null) {
                throw new ResourceNotFoundException("Profile", "slug", slug);
            }

            return result.toString();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Profile", "slug", slug);
        }
    }
}