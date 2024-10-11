package org.mrshoffen.exchange.utils.exchange.utils.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {

    private final List<String> validationErrors = new ArrayList<>();

    public static ValidationResult createEmpty() {
        return new ValidationResult();
    }

    public static ValidationResult of(String error) {
        ValidationResult vr = new ValidationResult();
        vr.validationErrors.add(error);
        return vr;
    }

    public ValidationResult and(ValidationResult another) {
        validationErrors.addAll(another.validationErrors);
        return this;
    }

    public boolean isNotValid() {
        return !validationErrors.isEmpty();
    }

    public String allValidatingErrors() {
        return String.join("\n", validationErrors);
    }

}
