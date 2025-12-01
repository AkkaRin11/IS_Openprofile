package ru.akarpo.openprofile.is_openprofile.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import ru.akarpo.openprofile.is_openprofile.converter.PublicationStatusConverter;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.enm.PublicationStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "publications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"profile_id", "version"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profile_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_publication_profile"))
    private Profile profile;

    @Convert(converter = PublicationStatusConverter.class)
    @Column(nullable = false, columnDefinition = "publication_status")
    private PublicationStatus status;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(nullable = false)
    private int version;

    @Type(JsonType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode snapshot;

    @Column(name = "published_at")
    private Instant publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}