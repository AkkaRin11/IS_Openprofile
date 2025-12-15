package ru.akarpo.openprofile.is_openprofile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akarpo.openprofile.is_openprofile.dto.UserDTO;
import ru.akarpo.openprofile.is_openprofile.schema.response.ApiResponse;
import ru.akarpo.openprofile.is_openprofile.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление учетными записями пользователей")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем авторизованном пользователе.")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .data(userService.getCurrentUser())
                .build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Получить пользователя по Email", description = "Возвращает публичную информацию о пользователе по его email адресу.")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                        .data(user)
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }
}