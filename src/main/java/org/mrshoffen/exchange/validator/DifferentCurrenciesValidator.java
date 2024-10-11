package org.mrshoffen.exchange.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;

public class DifferentCurrenciesValidator implements ConstraintValidator<ValidDifferentCurrenciesCode, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String base = null;
        String target = null;

        if (value instanceof ExchangeRateRequestDto dto) {
            base = dto.getBaseCurrency();
            target = dto.getTargetCurrency();
        }


        if (value instanceof ExchangeRequestDto dto) {
            base = dto.getBaseCurrencyCode();
            target = dto.getTargetCurrencyCode();
        }

        return (target != null && base != null && !target.equals(base));
    }
}
