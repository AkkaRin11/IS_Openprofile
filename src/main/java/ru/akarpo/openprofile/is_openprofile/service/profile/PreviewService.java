package ru.akarpo.openprofile.is_openprofile.service.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileMedia;
import ru.akarpo.openprofile.is_openprofile.domain.profile.ProfileWidget;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreviewService {

    private final ProfileRepository profileRepository;
    private final PrivacyControlService privacyControlService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> generatePreview(UUID profileId) {
        privacyControlService.enforceOwnership(profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        Map<String, Object> preview = new HashMap<>();
        preview.put("id", profile.getId());
        preview.put("name", profile.getName());
        preview.put("slug", profile.getSlug());
        preview.put("privacy", profile.getPrivacy().name());

        if (profile.getTheme() != null) {
            Map<String, Object> theme = new HashMap<>();
            theme.put("name", profile.getTheme().getName());
            theme.put("palette", profile.getTheme().getPalette());
            theme.put("typography", profile.getTheme().getTypography());
            preview.put("theme", theme);
        }

        List<Map<String, Object>> widgets = profile.getWidgets().stream()
                .sorted(Comparator.comparingInt(ProfileWidget::getPosition))
                .map(widget -> {
                    Map<String, Object> w = new HashMap<>();
                    w.put("id", widget.getId());
                    w.put("type", widget.getWidgetType().getCode());
                    w.put("title", widget.getTitle());
                    w.put("settings", widget.getSettings());
                    w.put("layout", widget.getLayout());
                    return w;
                })
                .collect(Collectors.toList());
        preview.put("widgets", widgets);

        List<Map<String, Object>> media = profile.getMedia().stream()
                .map(pm -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", pm.getMedia().getId());
                    m.put("role", pm.getRole());
                    m.put("isPrimary", pm.isPrimary());
                    m.put("storageKey", pm.getMedia().getStorageKey());
                    m.put("altText", pm.getMedia().getAltText());
                    return m;
                })
                .collect(Collectors.toList());
        preview.put("media", media);

        return preview;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateDevicePreview(UUID profileId, String deviceType) {
        if (!Arrays.asList("desktop", "tablet", "mobile").contains(deviceType.toLowerCase())) {
            throw new BadRequestException("Invalid device type. Allowed: desktop, tablet, mobile");
        }

        Map<String, Object> preview = generatePreview(profileId);
        preview.put("deviceType", deviceType);

        Map<String, Object> viewport = new HashMap<>();
        switch (deviceType.toLowerCase()) {
            case "desktop":
                viewport.put("width", 1920);
                viewport.put("height", 1080);
                break;
            case "tablet":
                viewport.put("width", 768);
                viewport.put("height", 1024);
                break;
            case "mobile":
                viewport.put("width", 375);
                viewport.put("height", 667);
                break;
        }
        preview.put("viewport", viewport);

        return preview;
    }
}