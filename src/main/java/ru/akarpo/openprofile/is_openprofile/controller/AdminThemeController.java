package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.ThemeDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.ThemeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/themes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Администрирование тем", description = "Управление глобальными темами оформления")
public class AdminThemeController {

    private final ThemeService themeService;

    @GetMapping
    @Operation(summary = "Получить все темы", description = "Возвращает список всех тем для администрирования.")
    public ResponseEntity<ApiResponse<List<ThemeDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<ThemeDTO>>builder()
                .data(themeService.findAll())
                .build());
    }

    @PostMapping
    @Operation(summary = "Создать тему", description = "Создает новую тему оформления доступную всем пользователям.")
    public ResponseEntity<ApiResponse<ThemeDTO>> create(@RequestBody ThemeDTO dto) {
        ThemeDTO saved = themeService.save(dto);
        return ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                .message("Theme created successfully")
                .data(saved)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тему", description = "Обновляет параметры существующей темы (цвета, шрифты).")
    public ResponseEntity<ApiResponse<ThemeDTO>> update(@PathVariable UUID id,
            @RequestBody ThemeDTO dto) {
        dto.setId(id);
        ThemeDTO saved = themeService.save(dto);
        return ResponseEntity.ok(ApiResponse.<ThemeDTO>builder()
                .message("Theme updated successfully")
                .data(saved)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тему", description = "Удаляет тему. Внимание: пользователи, использующие эту тему, могут потерять оформление.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        themeService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Theme deleted successfully")
                .build());
    }
}
