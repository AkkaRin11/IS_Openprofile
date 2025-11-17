package ru.akarpo.openprofile.is_openprofile.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.domain.profile.Profile;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;
import ru.akarpo.openprofile.is_openprofile.repository.profile.ProfileRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrivacyControlService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public boolean canAccessProfile(UUID profileId, String userEmail) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        if (profile.getPrivacy() == PrivacyLevel.PUBLIC) {
            return true;
        }

        if (userEmail == null) {
            return false;
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if (profile.getUser().getId().equals(user.getId())) {
            return true;
        }

        return profile.getPrivacy() == PrivacyLevel.UNLISTED;
    }

    public void enforceProfileAccess(UUID profileId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!canAccessProfile(profileId, email)) {
            throw new BadRequestException("Access denied to this profile");
        }
    }

    public void enforceOwnership(UUID profileId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        if (!profile.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You do not own this profile");
        }
    }
}