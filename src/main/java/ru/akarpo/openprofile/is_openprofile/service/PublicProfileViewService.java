package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.dto.profile.PublicProfileViewDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.profile.PublicProfileViewMapper;
import ru.akarpo.openprofile.is_openprofile.repository.profile.PublicProfileViewRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicProfileViewService {

    private final PublicProfileViewRepository publicProfileViewRepository;
    private final PublicProfileViewMapper publicProfileViewMapper;

    public List<PublicProfileViewDTO> findAll() {
        return publicProfileViewRepository.findAll().stream()
                .map(publicProfileViewMapper::toDto)
                .toList();
    }

    public Optional<PublicProfileViewDTO> findByPublicationId(UUID publicationId) {
        return publicProfileViewRepository.findByPublicationId(publicationId)
                .map(publicProfileViewMapper::toDto);
    }

    public Optional<PublicProfileViewDTO> findBySlug(String slug) {
        return publicProfileViewRepository.findBySlug(slug)
                .map(publicProfileViewMapper::toDto);
    }

    public Optional<PublicProfileViewDTO> findByProfileId(UUID profileId) {
        return publicProfileViewRepository.findByProfileId(profileId)
                .map(publicProfileViewMapper::toDto);
    }
}