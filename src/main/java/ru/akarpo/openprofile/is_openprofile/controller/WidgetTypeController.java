package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.widget.WidgetTypeDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.widget.WidgetTypeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/widget-types")
@RequiredArgsConstructor
@Tag(name = "Типы виджетов", description = "Справочник доступных типов виджетов и их схем")
public class WidgetTypeController {

    private final WidgetTypeService widgetTypeService;

    @GetMapping
    @Operation(summary = "Получить все типы виджетов", description = "Возвращает список всех типов виджетов, доступных для использования.")
    public ResponseEntity<ApiResponse<List<WidgetTypeDTO>>> getAllWidgetTypes() {
        List<WidgetTypeDTO> widgetTypes = widgetTypeService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<WidgetTypeDTO>>builder()
                .data(widgetTypes)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тип виджета по ID", description = "Возвращает информацию о типе виджета по его уникальному ID.")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> getWidgetTypeById(@PathVariable UUID id) {
        return widgetTypeService.findById(id)
                .map(widgetType -> ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                        .data(widgetType)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Получить тип виджета по коду", description = "Возвращает информацию о типе виджета по его уникальному коду.")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> getWidgetTypeByCode(@PathVariable String code) {
        return widgetTypeService.findByCode(code)
                .map(widgetType -> ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                        .data(widgetType)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
