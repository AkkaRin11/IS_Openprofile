package ru.akarpo.openprofile.is_openprofile.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final boolean emailVerified;
    private final boolean twoFactorEnabled;
    private final String twoFactorSecret;
    private final java.util.UUID id;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
            boolean emailVerified, boolean twoFactorEnabled, String twoFactorSecret, java.util.UUID id) {
        super(username, password, authorities);
        this.emailVerified = emailVerified;
        this.twoFactorEnabled = twoFactorEnabled;
        this.twoFactorSecret = twoFactorSecret;
        this.id = id;
    }
}
