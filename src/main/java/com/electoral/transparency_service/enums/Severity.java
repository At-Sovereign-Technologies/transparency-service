package com.electoral.transparency_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Severity levels for audit events.
 * Allows filtering and prioritization of audit logs in the immutable ledger.
 */
public enum Severity {
    INFO(0, "Informational"),
    LOW(1, "Low Priority"),
    MEDIUM(2, "Medium Priority"),
    HIGH(3, "High Priority"),
    CRITICAL(4, "Critical");

    private final int level;
    private final String description;

    Severity(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Severity fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        for (Severity severity : values()) {
            if (severity.name().equals(normalized)) {
                return severity;
            }

            if (String.valueOf(severity.level).equals(normalized)) {
                return severity;
            }
        }

        return Severity.valueOf(normalized);
    }
}
