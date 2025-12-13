package ru.akarpo.openprofile.is_openprofile.service.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileMapper;
import ru.akarpo.openprofile.is_openprofile.repository.ThemeRepository;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void save_ShouldCreateProfile_WhenNew() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ProfileDTO inputDto = ProfileDTO.builder()
                .name("New Profile")
                .slug("new-profile")
                .privacy(PrivacyLevel.PUBLIC)
                .userId(userId)
                .build();

        User user = new User();
        user.setId(userId);

        Profile savedProfile = new Profile();
        savedProfile.setId(UUID.randomUUID());
        savedProfile.setName("New Profile");

        ProfileDTO expectedDto = ProfileDTO.builder()
                .id(savedProfile.getId())
                .name("New Profile")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);
        when(profileMapper.toDto(savedProfile)).thenReturn(expectedDto);

        // Act
        ProfileDTO result = profileService.save(inputDto);

        // Assert
        assertThat(result.getId()).isEqualTo(savedProfile.getId());
        verify(userRepository).findById(userId);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void save_ShouldUpdateProfile_WhenExists() {
        // Arrange
        UUID profileId = UUID.randomUUID();
        ProfileDTO inputDto = ProfileDTO.builder()
                .id(profileId)
                .name("Updated Name")
                .privacy(PrivacyLevel.PRIVATE)
                .build();

        Profile existingProfile = new Profile();
        existingProfile.setId(profileId);
        existingProfile.setName("Old Name");

        Profile updatedProfile = new Profile(); // Mock result of save
        updatedProfile.setId(profileId);
        updatedProfile.setName("Updated Name");

        when(profileRepository.findById(profileId)).thenReturn(Optional.of(existingProfile));
        when(profileRepository.save(existingProfile)).thenReturn(updatedProfile);
        when(profileMapper.toDto(updatedProfile)).thenReturn(inputDto);

        // Act
        ProfileDTO result = profileService.save(inputDto);

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(profileRepository).findById(profileId);
    }

    @Test
    void findBySlug_ShouldReturnProfile_WhenExists() {
        // Arrange
        String slug = "my-slug";
        Profile profile = new Profile();
        ProfileDTO dto = ProfileDTO.builder().slug(slug).build();

        when(profileRepository.findBySlug(slug)).thenReturn(Optional.of(profile));
        when(profileMapper.toDto(profile)).thenReturn(dto);

        // Act
        Optional<ProfileDTO> result = profileService.findBySlug(slug);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo(slug);
    }
}
