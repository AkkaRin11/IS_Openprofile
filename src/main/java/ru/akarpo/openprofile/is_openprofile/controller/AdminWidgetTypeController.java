package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetTypeDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.widget.WidgetTypeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/widget-types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Администрирование", description = "Управление системными настройками и типами виджетов для администраторов")
public class AdminWidgetTypeController {

    private final WidgetTypeService widgetTypeService;

    @GetMapping
    @Operation(summary = "Получить все типы виджетов", description = "Возвращает полный список доступных типов виджетов в системе.")
    public ResponseEntity<ApiResponse<List<WidgetTypeDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.<List<WidgetTypeDTO>>builder()
                .data(widgetTypeService.findAll())
                .build());
    }

    @PostMapping
    @Operation(summary = "Создать тип виджета", description = "Добавляет новый тип виджета в систему. Требует прав администратора.")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> create(@RequestBody WidgetTypeDTO dto) {
        WidgetTypeDTO saved = widgetTypeService.save(dto);
        return ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                .message("Widget type created")
                .data(saved)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить тип виджета", description = "Обновляет существующий тип виджета по ID. Требует прав администратора.")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> update(@PathVariable UUID id,
            @RequestBody WidgetTypeDTO dto) {
        dto.setId(id);
        WidgetTypeDTO saved = widgetTypeService.save(dto);
        return ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                .message("Widget type updated")
                .data(saved)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить тип виджета", description = "Удаляет тип виджета из системы по ID. Требует прав администратора.")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        widgetTypeService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Widget type deleted")
                .build());
    }
}
