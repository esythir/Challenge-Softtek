package br.com.fiap.challenge_softteck.domain.valueobject;

import java.util.UUID;

/**
 * Value Object que representa o ID de um usuário.
 * Para NoSQL, usamos String em vez de bytes.
 */
public record UserId(String value) {

    public UserId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }

    /**
     * Cria um UserId a partir de um UUID
     */
    public static UserId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        return new UserId(uuid.toString());
    }

    /**
     * Cria um UserId a partir de uma string
     */
    public static UserId fromString(String id) {
        return new UserId(id);
    }

    /**
     * Converte o UserId para UUID (se for um UUID válido)
     */
    public UUID toUuid() {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("User ID is not a valid UUID: " + value, e);
        }
    }

    /**
     * Verifica se o UserId é um UUID válido
     */
    public boolean isUuid() {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}