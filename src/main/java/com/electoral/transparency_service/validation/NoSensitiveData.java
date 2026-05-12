package com.electoral.transparency_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for Zero-Identity Enforcement.
 * Ensures that details map fields do not contain PII field names.
 */
@Constraint(validatedBy = SensitiveDataValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSensitiveData {
    String message() default "Details map contains forbidden field names";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
