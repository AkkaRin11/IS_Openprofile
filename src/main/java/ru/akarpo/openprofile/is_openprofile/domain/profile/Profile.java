package ru.akarpo.openprofile.is_openprofile.domain.profile;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import ru.akarpo.openprofile.is_openprofile.converter.PrivacyLevelConverter;
import ru.akarpo.openprofile.is_openprofile.domain.Publication;
import ru.akarpo.openprofile.is_openprofile.domain.Theme;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

        @Id
        @GeneratedValue
        @UuidGenerator(style = UuidGenerator.Style.RANDOM)
        private UUID id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_profile_user"))
        private User user;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false, unique = true)
        private String slug;

        @Column(name = "description")
        private String description;

        @Column(name = "image")
        private String image;

        @Convert(converter = PrivacyLevelConverter.class)
        @Column(nullable = false, columnDefinition = "privacy_level")
        private PrivacyLevel privacy;

        @ManyToOne
        @JoinColumn(name = "theme_id", foreignKey = @ForeignKey(name = "fk_profile_theme"))
        private Theme theme;

        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;

        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<ProfileWidget> widgets = new ArrayList<>();

        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<ProfileMedia> media = new ArrayList<>();

        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<Publication> publications = new ArrayList<>();
}