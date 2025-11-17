package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.Theme;
import ru.akarpo.openprofile.is_openprofile.dto.ThemeDTO;
import ru.akarpo.openprofile.is_openprofile.mapper.ThemeMapper;
import ru.akarpo.openprofile.is_openprofile.repository.ThemeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;

    public List<ThemeDTO> findAll() {
        return themeRepository.findAll().stream()
                .map(themeMapper::toDto)
                .toList();
    }

    public Optional<ThemeDTO> findById(UUID id) {
        return themeRepository.findById(id)
                .map(themeMapper::toDto);
    }

    public Optional<ThemeDTO> findByName(String name) {
        return themeRepository.findByName(name)
                .map(themeMapper::toDto);
    }

    public ThemeDTO save(ThemeDTO themeDTO) {
        Theme theme = themeMapper.toEntity(themeDTO);
        Theme saved = themeRepository.save(theme);
        return themeMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        themeRepository.deleteById(id);
    }
}