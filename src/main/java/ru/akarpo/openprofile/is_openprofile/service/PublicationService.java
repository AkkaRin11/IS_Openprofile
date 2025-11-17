package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.Publication;
import ru.akarpo.openprofile.is_openprofile.dto.PublicationDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.PublicationMapper;
import ru.akarpo.openprofile.is_openprofile.repository.PublicationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;

    public List<PublicationDTO> findAll() {
        return publicationRepository.findAll().stream()
                .map(publicationMapper::toDto)
                .toList();
    }

    public Optional<PublicationDTO> findById(UUID id) {
        return publicationRepository.findById(id)
                .map(publicationMapper::toDto);
    }

    public List<PublicationDTO> findByProfileId(UUID profileId) {
        return publicationRepository.findByProfileId(profileId).stream()
                .map(publicationMapper::toDto)
                .toList();
    }

    public Optional<PublicationDTO> findActiveByProfileId(UUID profileId) {
        return publicationRepository.findByProfileIdAndActiveTrue(profileId)
                .map(publicationMapper::toDto);
    }

    public PublicationDTO save(PublicationDTO publicationDTO) {
        Publication publication = publicationMapper.toEntity(publicationDTO);
        Publication saved = publicationRepository.save(publication);
        return publicationMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        publicationRepository.deleteById(id);
    }
}