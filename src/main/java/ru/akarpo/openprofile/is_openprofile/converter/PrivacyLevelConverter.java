package ru.akarpo.openprofile.is_openprofile.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.akarpo.openprofile.is_openprofile.enm.PrivacyLevel;

@Converter(autoApply = true)
public class PrivacyLevelConverter implements AttributeConverter<PrivacyLevel, String> {

    @Override
    public String convertToDatabaseColumn(PrivacyLevel attribute) {
        if (attribute == null) {
            return "public";
        }
        return attribute.getDbValue();
    }

    @Override
    public PrivacyLevel convertToEntityAttribute(String dbData) {
        return PrivacyLevel.fromDbValue(dbData);
    }
}