package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.MediaAssetMapper;
import ru.akarpo.openprofile.is_openprofile.repository.MediaAssetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaAssetService {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetMapper mediaAssetMapper;

    public List<MediaAssetDTO> findAll() {
        return mediaAssetRepository.findAll().stream()
                .map(mediaAssetMapper::toDto)
                .toList();
    }

    public Optional<MediaAssetDTO> findById(UUID id) {
        return mediaAssetRepository.findById(id)
                .map(mediaAssetMapper::toDto);
    }

    public List<MediaAssetDTO> findByUserId(UUID userId) {
        return mediaAssetRepository.findByUserId(userId).stream()
                .map(mediaAssetMapper::toDto)
                .toList();
    }

    public MediaAssetDTO save(MediaAssetDTO mediaAssetDTO) {
        MediaAsset mediaAsset = mediaAssetMapper.toEntity(mediaAssetDTO);
        MediaAsset saved = mediaAssetRepository.save(mediaAsset);
        return mediaAssetMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        mediaAssetRepository.deleteById(id);
    }
}