package ru.akarpo.openprofile.is_openprofile.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMediaId;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileMediaDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.ProfileMediaMapper;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileMediaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileMediaService {

    private final ProfileMediaRepository profileMediaRepository;
    private final ProfileMediaMapper profileMediaMapper;

    public List<ProfileMediaDTO> findAll() {
        return profileMediaRepository.findAll().stream()
                .map(profileMediaMapper::toDto)
                .toList();
    }

    public Optional<ProfileMediaDTO> findById(UUID profileId, UUID mediaId) {
        return profileMediaRepository.findByProfileIdAndMediaId(profileId, mediaId)
                .map(profileMediaMapper::toDto);
    }

    public List<ProfileMediaDTO> findByProfileId(UUID profileId) {
        return profileMediaRepository.findByProfileId(profileId).stream()
                .map(profileMediaMapper::toDto)
                .toList();
    }

    public List<ProfileMediaDTO> findByMediaId(UUID mediaId) {
        return profileMediaRepository.findByMediaId(mediaId).stream()
                .map(profileMediaMapper::toDto)
                .toList();
    }

    public ProfileMediaDTO save(ProfileMediaDTO profileMediaDTO) {
        ProfileMedia profileMedia = profileMediaMapper.toEntity(profileMediaDTO);
        ProfileMedia saved = profileMediaRepository.save(profileMedia);
        return profileMediaMapper.toDto(saved);
    }

    public void deleteById(UUID profileId, UUID mediaId) {
        ProfileMediaId id = new ProfileMediaId(profileId, mediaId);
        profileMediaRepository.deleteById(id);
    }
}
