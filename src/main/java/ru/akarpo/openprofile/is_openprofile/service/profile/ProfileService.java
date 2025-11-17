package ru.akarpo.openprofile.is_openprofile.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileMapper;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    public List<ProfileDTO> findAll() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toDto)
                .toList();
    }

    public Optional<ProfileDTO> findById(UUID id) {
        return profileRepository.findById(id)
                .map(profileMapper::toDto);
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

    public ProfileDTO save(ProfileDTO profileDTO) {
        Profile profile = profileMapper.toEntity(profileDTO);
        Profile saved = profileRepository.save(profile);
        return profileMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        profileRepository.deleteById(id);
    }
}