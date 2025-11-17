package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.ExternalService;
import ru.akarpo.openprofile.is_openprofile.dto.ExternalServiceDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.ExternalServiceMapper;
import ru.akarpo.openprofile.is_openprofile.repository.ExternalServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExternalServiceService {

    private final ExternalServiceRepository externalServiceRepository;
    private final ExternalServiceMapper externalServiceMapper;

    public List<ExternalServiceDTO> findAll() {
        return externalServiceRepository.findAll().stream()
                .map(externalServiceMapper::toDto)
                .toList();
    }

    public Optional<ExternalServiceDTO> findById(UUID id) {
        return externalServiceRepository.findById(id)
                .map(externalServiceMapper::toDto);
    }

    public Optional<ExternalServiceDTO> findByCode(String code) {
        return externalServiceRepository.findByCode(code)
                .map(externalServiceMapper::toDto);
    }

    public ExternalServiceDTO save(ExternalServiceDTO externalServiceDTO) {
        ExternalService externalService = externalServiceMapper.toEntity(externalServiceDTO);
        ExternalService saved = externalServiceRepository.save(externalService);
        return externalServiceMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        externalServiceRepository.deleteById(id);
    }
}