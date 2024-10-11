package org.mrshoffen.exchange.utils.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValResult {

    private final List<String> validationErrors = new ArrayList<>();

    public static ValResult createEmpty() {
        return new ValResult();
    }

    public static ValResult of(String error) {
        ValResult vr = new ValResult();
        vr.validationErrors.add(error);
        return vr;
    }

    public ValResult and(ValResult another) {
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
