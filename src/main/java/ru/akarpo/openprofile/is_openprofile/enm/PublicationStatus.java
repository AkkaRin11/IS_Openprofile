package ru.akarpo.openprofile.is_openprofile.enm;

import lombok.Getter;

@Getter
public enum PublicationStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    ARCHIVED("archived");

    private final String dbValue;

    PublicationStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public static PublicationStatus fromDbValue(String value) {
        if (value == null) return DRAFT;
        for (PublicationStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return DRAFT;
    }
}