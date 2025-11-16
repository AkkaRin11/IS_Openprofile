package ru.akarpo.openprofile.is_openprofile.domain;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Immutable
@Table(name = "v_public_profiles")
public class PublicProfileView {

    @Id
    @Column(name = "publication_id")
    private UUID publicationId;

    @Column(name = "slug")
    private String slug;

    @Column(name = "profile_id")
    private UUID profileId;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "snapshot")
    private Map<String, Object> snapshot;
}