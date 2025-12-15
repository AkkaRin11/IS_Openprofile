package ru.akarpo.openprofile.is_openprofile.service;

import com.google.common.io.BaseEncoding;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;

@Service
public class TwoFactorService {

    private final SecureRandom random = new SecureRandom();

    public String generateSecret() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return BaseEncoding.base32().encode(bytes).replace("=", "");
    }

    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || !code.matches("\\d{6}")) {
            return false;
        }

        long timeIndex = Instant.now().getEpochSecond() / 30;

        // Check current interval and previous one (for drift)
        for (int i = 0; i >= -1; i--) {
            if (getCode(secret, timeIndex + i).equals(code)) {
                return true;
            }
        }
        return false;
    }

    private String getCode(String secret, long timeIndex) {
        try {
            byte[] key = BaseEncoding.base32().decode(secret);
            byte[] data = new byte[8];
            for (int i = 8; i-- > 0; timeIndex >>>= 8) {
                data[i] = (byte) timeIndex;
            }

            SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signKey);
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;

            return String.format("%06d", truncatedHash);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            return "";
        }
    }
}