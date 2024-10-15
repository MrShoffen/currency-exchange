package org.mrshoffen.exchange.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class Iso4217Validator implements ConstraintValidator<ValidIso4217, String> {
    private static final Set<String> ALL_CURRENCIES_CODES = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ALL_CURRENCIES_CODES.contains(value);
    }
}
