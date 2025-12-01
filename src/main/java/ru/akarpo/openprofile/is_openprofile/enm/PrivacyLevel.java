package ru.akarpo.openprofile.is_openprofile.enm;

import lombok.Getter;

@Getter
public enum PrivacyLevel {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private");

    private final String dbValue;

    PrivacyLevel(String dbValue) {
        this.dbValue = dbValue;
    }

    public static PrivacyLevel fromDbValue(String value) {
        if (value == null) return PUBLIC;
        for (PrivacyLevel level : values()) {
            if (level.dbValue.equalsIgnoreCase(value)) {
                return level;
            }
        }
        return PUBLIC;
    }
}