package ru.akarpo.openprofile.is_openprofile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.akarpo.openprofile.is_openprofile.dto.profile.ProfileDTO;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.schema.request.CreateProfileRequest;
import ru.akarpo.openprofile.is_openprofile.service.profile.ProfileService;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.security.JwtService;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProfileService profileService;

        @MockitoBean
        private UserRepository userRepository;

        @MockitoBean
        private JwtService jwtService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void getProfileById_ShouldReturnProfile() throws Exception {
                UUID id = UUID.randomUUID();
                ProfileDTO dto = ProfileDTO.builder()
                                .id(id)
                                .name("Test Profile")
                                .build();

                when(profileService.findById(id)).thenReturn(Optional.of(dto));

                mockMvc.perform(get("/api/profiles/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value(id.toString()))
                                .andExpect(jsonPath("$.data.name").value("Test Profile"));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void createProfile_ShouldCreate_WhenAuthenticated() throws Exception {
                CreateProfileRequest request = new CreateProfileRequest();
                request.setName("My New Profile");
                request.setSlug("my-new-profile");

                User mockUser = new User();
                mockUser.setId(UUID.randomUUID());
                mockUser.setEmail("test@example.com");

                when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

                ProfileDTO responseDto = ProfileDTO.builder()
                                .id(UUID.randomUUID())
                                .name("My New Profile")
                                .slug("my-new-profile")
                                .build();

                when(profileService.save(any(ProfileDTO.class))).thenReturn(responseDto);

                mockMvc.perform(post("/api/profiles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name").value("My New Profile"));
        }
}
