package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileWidgetDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileWidgetMapper;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileWidgetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileWidgetService {

    private final ProfileWidgetRepository profileWidgetRepository;
    private final ProfileWidgetMapper profileWidgetMapper;

    public List<ProfileWidgetDTO> findAll() {
        return profileWidgetRepository.findAll().stream()
                .map(profileWidgetMapper::toDto)
                .toList();
    }

    public Optional<ProfileWidgetDTO> findById(UUID id) {
        return profileWidgetRepository.findById(id)
                .map(profileWidgetMapper::toDto);
    }

    public List<ProfileWidgetDTO> findByProfileId(UUID profileId) {
        return profileWidgetRepository.findByProfileId(profileId).stream()
                .map(profileWidgetMapper::toDto)
                .toList();
    }

    public ProfileWidgetDTO save(ProfileWidgetDTO profileWidgetDTO) {
        ProfileWidget profileWidget = profileWidgetMapper.toEntity(profileWidgetDTO);
        ProfileWidget saved = profileWidgetRepository.save(profileWidget);
        return profileWidgetMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        profileWidgetRepository.deleteById(id);
    }
}
