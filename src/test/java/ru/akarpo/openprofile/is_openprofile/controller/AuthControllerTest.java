package ru.akarpo.openprofile.is_openprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.akarpo.openprofile.is_openprofile.schema.request.LoginRequest;
import ru.akarpo.openprofile.is_openprofile.schema.request.RegisterRequest;
import ru.akarpo.openprofile.is_openprofile.schema.response.AuthResponse;
import ru.akarpo.openprofile.is_openprofile.service.AuthService;
import ru.akarpo.openprofile.is_openprofile.security.JwtService;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        // Mocking other potential dependencies of AuthController if any, or Security
        @MockitoBean
        private JwtService jwtService; // Often needed if security is active, though addFilters=false might skip it.

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void register_ShouldReturn200_WhenValid() throws Exception {
                RegisterRequest request = new RegisterRequest();
                request.setEmail("new@example.com");
                request.setPassword("password123");

                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token")
                                .userId(UUID.randomUUID())
                                .email("new@example.com")
                                .build();

                when(authService.register(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.token").value("jwt-token"));
        }

        @Test
        void login_ShouldReturn200_WhenValid() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setEmail("user@example.com");
                request.setPassword("password123");

                AuthResponse response = AuthResponse.builder()
                                .token("jwt-token")
                                .build();

                when(authService.login(any())).thenReturn(response);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.token").value("jwt-token"));
        }
}
