package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.schema.request.AddWidgetRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.BindWidgetRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.ConnectServiceRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.profile.ProfileManagementService;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile-management")
@RequiredArgsConstructor
@Tag(name = "Создание профиля", description = "Операции для управления профилями с использованием функций PostgreSQL")
public class ProfileManagementController {

        private final ProfileManagementService profileManagementService;

        @PostMapping("/profiles/{profileId}/widgets")
        @Operation(summary = "Добавить виджет в профиль", description = "Добавляет новый виджет в профиль. Эта операция создает экземпляр типа виджета для указанного профиля.")
        public ResponseEntity<ApiResponse<UUID>> addWidget(
                        @PathVariable UUID profileId,
                        @RequestBody AddWidgetRequest request) {

                UUID widgetId = profileManagementService.addWidget(
                                profileId,
                                request.getWidgetCode(),
                                request.getTitle(),
                                request.getSettings(),
                                request.getLayout());

                return ResponseEntity.ok(ApiResponse.<UUID>builder()
                                .message("Widget added successfully")
                                .data(widgetId)
                                .build());
        }

        @PostMapping("/users/{userId}/connect-service")
        @Operation(summary = "Подключить внешний сервис", description = "Подключает внешний сервис (например, GitHub, Spotify) к учетной записи пользователя для использования в виджетах.")
        public ResponseEntity<ApiResponse<UUID>> connectService(
                        @PathVariable UUID userId,
                        @RequestBody ConnectServiceRequest request) {

                UUID connectionId = profileManagementService.connectService(
                                userId,
                                request.getServiceCode(),
                                request.getExternalUserId(),
                                request.getAccessToken(),
                                request.getRefreshToken(),
                                request.getTokenExpiresAt());

                return ResponseEntity.ok(ApiResponse.<UUID>builder()
                                .message("Service connected successfully")
                                .data(connectionId)
                                .build());
        }

        @PostMapping("/widgets/{widgetId}/bind")
        @Operation(summary = "Привязать виджет к подключению", description = "Связывает виджет профиля с конкретным подключением к сервису для отображения данных.")
        public ResponseEntity<ApiResponse<Void>> bindWidget(
                        @PathVariable UUID widgetId,
                        @RequestBody BindWidgetRequest request) {

                profileManagementService.bindWidgetToConnection(widgetId, request.getConnectionId());

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .message("Widget bound to connection successfully")
                                .build());
        }

        @PostMapping("/profiles/{profileId}/publish")
        @Operation(summary = "Опубликовать профиль", description = "Публикует профиль, делая его доступным для просмотра в соответствии с настройками видимости. При необходимости генерирует статический снимок.")
        public ResponseEntity<ApiResponse<String>> publishProfile(@PathVariable UUID profileId) {
                String slug = profileManagementService.publishProfile(profileId);

                return ResponseEntity.ok(ApiResponse.<String>builder()
                                .message("Profile published successfully")
                                .data(slug)
                                .build());
        }

        @GetMapping("/public/{slug}")
        @Operation(summary = "Получить публичный профиль", description = "Возвращает публичное представление профиля по его slug. Возвращает агрегированные данные профиля, включая виджеты и тему.")
        public ResponseEntity<ApiResponse<String>> getPublicProfile(@PathVariable String slug) {
                String snapshot = profileManagementService.getPublicProfile(slug);

                return ResponseEntity.ok(ApiResponse.<String>builder()
                                .data(snapshot)
                                .build());
        }
}