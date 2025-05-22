package br.com.fiap.challenge_softteck.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YnBooleanConverterUtil implements AttributeConverter<Boolean,String> {
    @Override public String convertToDatabaseColumn(Boolean attr) {
        return Boolean.TRUE.equals(attr) ? "Y" : "N";
    }
    @Override public Boolean convertToEntityAttribute(String db) {
        return "Y".equals(db);
    }
}