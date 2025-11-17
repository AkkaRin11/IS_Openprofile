package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.ServiceConnection;
import ru.akarpo.openprofile.is_openprofile.dto.ServiceConnectionDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.ServiceConnectionMapper;
import ru.akarpo.openprofile.is_openprofile.repository.ServiceConnectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceConnectionService {

    private final ServiceConnectionRepository serviceConnectionRepository;
    private final ServiceConnectionMapper serviceConnectionMapper;

    public List<ServiceConnectionDTO> findAll() {
        return serviceConnectionRepository.findAll().stream()
                .map(serviceConnectionMapper::toDto)
                .toList();
    }

    public Optional<ServiceConnectionDTO> findById(UUID id) {
        return serviceConnectionRepository.findById(id)
                .map(serviceConnectionMapper::toDto);
    }

    public List<ServiceConnectionDTO> findByUserId(UUID userId) {
        return serviceConnectionRepository.findByUserId(userId).stream()
                .map(serviceConnectionMapper::toDto)
                .toList();
    }

    public List<ServiceConnectionDTO> findByServiceId(UUID serviceId) {
        return serviceConnectionRepository.findByServiceId(serviceId).stream()
                .map(serviceConnectionMapper::toDto)
                .toList();
    }

    public Optional<ServiceConnectionDTO> findByUserAndServiceAndExternalUserId(UUID userId, UUID serviceId, String externalUserId) {
        return serviceConnectionRepository.findByUserIdAndServiceIdAndExternalUserId(userId, serviceId, externalUserId)
                .map(serviceConnectionMapper::toDto);
    }

    public ServiceConnectionDTO save(ServiceConnectionDTO serviceConnectionDTO) {
        ServiceConnection serviceConnection = serviceConnectionMapper.toEntity(serviceConnectionDTO);
        ServiceConnection saved = serviceConnectionRepository.save(serviceConnection);
        return serviceConnectionMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        serviceConnectionRepository.deleteById(id);
    }
}