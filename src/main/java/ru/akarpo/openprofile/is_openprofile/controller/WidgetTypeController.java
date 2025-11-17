package ru.akarpo.openprofile.is_openprofile.controller;

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
public class WidgetTypeController {

    private final WidgetTypeService widgetTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WidgetTypeDTO>>> getAllWidgetTypes() {
        List<WidgetTypeDTO> widgetTypes = widgetTypeService.findAll();
        return ResponseEntity.ok(ApiResponse.<List<WidgetTypeDTO>>builder()
                .data(widgetTypes)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> getWidgetTypeById(@PathVariable UUID id) {
        return widgetTypeService.findById(id)
                .map(widgetType -> ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                        .data(widgetType)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<WidgetTypeDTO>> getWidgetTypeByCode(@PathVariable String code) {
        return widgetTypeService.findByCode(code)
                .map(widgetType -> ResponseEntity.ok(ApiResponse.<WidgetTypeDTO>builder()
                        .data(widgetType)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
