package ru.akarpo.openprofile.is_openprofile.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileWidgetDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileWidgetMapper;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileWidgetRepository;

import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;

import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileWidgetService {

    private final ProfileWidgetRepository profileWidgetRepository;
    private final ProfileWidgetMapper profileWidgetMapper;
    private final ProfileRepository profileRepository;

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

    @Transactional
    public ProfileWidgetDTO save(ProfileWidgetDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException(
                    "Creation is not supported via this service. Use ProfileManagementService.");
        }

        ProfileWidget existing = profileWidgetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("ProfileWidget", "id", dto.getId()));

        if (dto.getTitle() != null)
            existing.setTitle(dto.getTitle());
        if (dto.getSettings() != null)
            existing.setSettings(dto.getSettings());
        if (dto.getLayout() != null)
            existing.setLayout(dto.getLayout());

        Integer newPos = dto.getPosition();
        if (newPos != null && newPos != existing.getPosition()) {
            UUID profileId = existing.getProfile().getId();
            // Lock the profile to serialize reordering operations and prevent deadlocks
            profileRepository.findByIdLocked(profileId);

            int oldPos = existing.getPosition();

            // 1. Move current widget out of the way
            existing.setPosition(-1);
            profileWidgetRepository.saveAndFlush(existing);

            // 2. Shift others
            if (newPos < oldPos) {
                profileWidgetRepository.incrementPositionsBetween(profileId, newPos, oldPos - 1);
            } else {
                profileWidgetRepository.decrementPositionsBetween(profileId, oldPos + 1, newPos);
            }

            existing.setPosition(newPos);
        }

        ProfileWidget saved = profileWidgetRepository.save(existing);
        return profileWidgetMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        profileWidgetRepository.deleteById(id);
    }
}
