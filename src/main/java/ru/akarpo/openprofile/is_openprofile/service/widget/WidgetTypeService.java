package ru.akarpo.openprofile.is_openprofile.service.widget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.widget.WidgetType;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetTypeDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.widget.WidgetTypeMapper;
import ru.akarpo.openprofile.is_openprofile.repository.widget.WidgetTypeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WidgetTypeService {

    private final WidgetTypeRepository widgetTypeRepository;
    private final WidgetTypeMapper widgetTypeMapper;

    public List<WidgetTypeDTO> findAll() {
        return widgetTypeRepository.findAll().stream()
                .map(widgetTypeMapper::toDto)
                .toList();
    }

    public Optional<WidgetTypeDTO> findById(UUID id) {
        return widgetTypeRepository.findById(id)
                .map(widgetTypeMapper::toDto);
    }

    public Optional<WidgetTypeDTO> findByCode(String code) {
        return widgetTypeRepository.findByCode(code)
                .map(widgetTypeMapper::toDto);
    }

    public List<WidgetTypeDTO> findBySupportsBinding(boolean supportsBinding) {
        return widgetTypeRepository.findBySupportsBinding(supportsBinding).stream()
                .map(widgetTypeMapper::toDto)
                .toList();
    }

    public WidgetTypeDTO save(WidgetTypeDTO widgetTypeDTO) {
        WidgetType widgetType = widgetTypeMapper.toEntity(widgetTypeDTO);
        WidgetType saved = widgetTypeRepository.save(widgetType);
        return widgetTypeMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        widgetTypeRepository.deleteById(id);
    }
}