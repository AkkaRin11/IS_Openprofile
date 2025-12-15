package ru.akarpo.openprofile.is_openprofile.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.domain.Theme;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileMapper;
import ru.akarpo.openprofile.is_openprofile.repository.ThemeRepository;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ProfileMapper profileMapper;

    public List<ProfileDTO> findAll() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toDto)
                .toList();
    }

    public ProfileDTO findByIdOrThrow(UUID id) {
        return profileRepository.findById(id)
                .map(profileMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));
    }

    public Optional<ProfileDTO> findById(UUID id) {
        return profileRepository.findById(id)
                .map(profileMapper::toDto);
    }

    public ProfileDTO findBySlugOrThrow(String slug) {
        return profileRepository.findBySlug(slug)
                .map(profileMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "slug", slug));
    }

    public Optional<ProfileDTO> findBySlug(String slug) {
        return profileRepository.findBySlug(slug)
                .map(profileMapper::toDto);
    }

    public List<ProfileDTO> findByUserId(UUID userId) {
        return profileRepository.findByUserId(userId).stream()
                .map(profileMapper::toDto)
                .toList();
    }

    @Transactional
    public ProfileDTO save(ProfileDTO profileDTO) {
        Profile profile;

        if (profileDTO.getId() != null) {
            profile = profileRepository.findById(profileDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileDTO.getId()));
            profile.setName(profileDTO.getName());
            profile.setPrivacy(profileDTO.getPrivacy());
            profile.setDescription(profileDTO.getDescription());
            profile.setImage(profileDTO.getImage());
        } else {
            profile = new Profile();
            profile.setName(profileDTO.getName());
            profile.setSlug(profileDTO.getSlug());
            profile.setPrivacy(profileDTO.getPrivacy());
            profile.setDescription(profileDTO.getDescription());
            profile.setImage(profileDTO.getImage());

            if (profileDTO.getUserId() != null) {
                User user = userRepository.findById(profileDTO.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", profileDTO.getUserId()));
                profile.setUser(user);
            }
        }

        if (profileDTO.getThemeId() != null) {
            Theme theme = themeRepository.findById(profileDTO.getThemeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", profileDTO.getThemeId()));
            profile.setTheme(theme);
        } else {
            profile.setTheme(null);
        }

        Profile saved = profileRepository.save(profile);
        return profileMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        if (!profileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profile", "id", id);
        }
        profileRepository.deleteById(id);
    }
}
