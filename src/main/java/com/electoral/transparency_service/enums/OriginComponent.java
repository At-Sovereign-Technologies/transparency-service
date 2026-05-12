package com.electoral.transparency_service.enums;

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
}
