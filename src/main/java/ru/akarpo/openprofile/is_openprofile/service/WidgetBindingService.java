package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetBinding;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetBindingDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.widget.WidgetBindingMapper;
import ru.akarpo.openprofile.is_openprofile.repository.widget.WidgetBindingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WidgetBindingService {

    private final WidgetBindingRepository widgetBindingRepository;
    private final WidgetBindingMapper widgetBindingMapper;

    public List<WidgetBindingDTO> findAll() {
        return widgetBindingRepository.findAll().stream()
                .map(widgetBindingMapper::toDto)
                .toList();
    }

    public Optional<WidgetBindingDTO> findById(UUID profileWidgetId, UUID connectionId) {
        return widgetBindingRepository.findByProfileWidgetIdAndConnectionId(profileWidgetId, connectionId)
                .map(widgetBindingMapper::toDto);
    }

    public List<WidgetBindingDTO> findByProfileWidgetId(UUID profileWidgetId) {
        return widgetBindingRepository.findByProfileWidgetId(profileWidgetId).stream()
                .map(widgetBindingMapper::toDto)
                .toList();
    }

    public List<WidgetBindingDTO> findByConnectionId(UUID connectionId) {
        return widgetBindingRepository.findByConnectionId(connectionId).stream()
                .map(widgetBindingMapper::toDto)
                .toList();
    }

    public WidgetBindingDTO save(WidgetBindingDTO widgetBindingDTO) {
        WidgetBinding widgetBinding = widgetBindingMapper.toEntity(widgetBindingDTO);
        WidgetBinding saved = widgetBindingRepository.save(widgetBinding);
        return widgetBindingMapper.toDto(saved);
    }

    public void deleteById(UUID profileWidgetId, UUID connectionId) {
        widgetBindingRepository.deleteByProfileWidgetIdAndConnectionId(profileWidgetId, connectionId);
    }
}