package ru.akarpo.openprofile.is_openprofile.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorService {

    private final LoadingCache<UUID, String> secretCache;
    private final SecureRandom random = new SecureRandom();

    public TwoFactorService() {
        this.secretCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<UUID, String>() {
                    @Override
                    public String load(UUID key) {
                        return generateRandomSecret();
                    }
                });
    }

    public String generateSecret(UUID userId) {
        String secret = generateRandomSecret();
        secretCache.put(userId, secret);
        return secret;
    }

    public boolean verifyCode(UUID userId, String code) {
        try {
            String secret = secretCache.getIfPresent(userId);
            if (secret == null) {
                return false;
            }
            return code.length() == 6 && code.matches("\\d+");
        } catch (Exception e) {
            return false;
        }
    }

    private String generateRandomSecret() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}