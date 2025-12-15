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

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT pw FROM ProfileWidget pw JOIN pw.bindings b")
    List<ProfileWidget> findAllWithBindings();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProfileWidget w SET w.position = w.position + 1 WHERE w.profile.id = :profileId AND w.position >= :start AND w.position <= :end")
    void incrementPositionsBetween(UUID profileId, int start, int end);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ProfileWidget w SET w.position = w.position - 1 WHERE w.profile.id = :profileId AND w.position >= :start AND w.position <= :end")
    void decrementPositionsBetween(UUID profileId, int start, int end);
}