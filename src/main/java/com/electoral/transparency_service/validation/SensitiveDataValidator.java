package com.electoral.transparency_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Set;

/**
 * Validator for the @NoSensitiveData constraint.
 * Ensures the details map does not contain restricted PII field names.
 */
public class SensitiveDataValidator implements ConstraintValidator<NoSensitiveData, Map<String, Object>> {

    private static final Set<String> FORBIDDEN_FIELDS = Set.of(
            "voter_id",
            "citizen_name",
            "document_number",
            "cedula",
            "passport",
            "ssn",
            "social_security",
            "email",
            "phone",
            "address",
            "fingerprint",
            "biometric"
    );

    @Override
    public boolean isValid(Map<String, Object> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        for (String fieldName : value.keySet()) {
            if (FORBIDDEN_FIELDS.contains(fieldName.toLowerCase())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        String.format("Field '%s' is not allowed in details map (Zero-Identity Enforcement)", fieldName)
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
