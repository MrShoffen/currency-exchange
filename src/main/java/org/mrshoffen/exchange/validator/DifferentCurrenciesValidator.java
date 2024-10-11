package org.mrshoffen.exchange.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;

public class DifferentCurrenciesValidator implements ConstraintValidator<ValidDifferentCurrenciesCode, ExchangeRateRequestDto> {

    @Override
    public boolean isValid(ExchangeRateRequestDto value, ConstraintValidatorContext context) {
        String base = value.getBaseCurrency();
        String target = value.getTargetCurrency();

        return (target!=null && base!=null && !target.equals(base));
    }
}
