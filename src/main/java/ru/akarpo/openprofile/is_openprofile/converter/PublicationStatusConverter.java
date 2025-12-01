package ru.akarpo.openprofile.is_openprofile.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.akarpo.openprofile.is_openprofile.enm.PublicationStatus;

@Converter(autoApply = true)
public class PublicationStatusConverter implements AttributeConverter<PublicationStatus, String> {

    @Override
    public String convertToDatabaseColumn(PublicationStatus attribute) {
        if (attribute == null) {
            return "draft";
        }
        return attribute.getDbValue();
    }

    @Override
    public PublicationStatus convertToEntityAttribute(String dbData) {
        return PublicationStatus.fromDbValue(dbData);
    }
}