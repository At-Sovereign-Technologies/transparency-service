package com.electoral.transparency_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enumeration of microservices that originate audit events.
 * Represents the identity provider for distributed audit trail.
 */
public enum OriginComponent {
    GESTION_PREELECTORAL("Pre-electoral Management"),
    COMPUTE_ENGINE("Vote Computation Engine"),
    GATEWAY("API Gateway");

    private final String displayName;

    OriginComponent(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static OriginComponent fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim()
                .toUpperCase()
                .replace('-', '_')
                .replace(' ', '_');

        for (OriginComponent component : values()) {
            if (component.name().equals(normalized)) {
                return component;
            }

            String displayNameNormalized = component.displayName
                    .toUpperCase()
                    .replace('-', '_')
                    .replace(' ', '_');

            if (displayNameNormalized.equals(normalized)) {
                return component;
            }
        }

        return OriginComponent.valueOf(normalized);
    }
}
