package org.mrshoffen.exchange.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.HibernateValidator;
import org.mrshoffen.exchange.validator.ValidDifferentCurrenciesCode;
import org.mrshoffen.exchange.validator.ValidIso4217;
import org.mrshoffen.exchange.validator.ValidNumberFormat;

import java.util.Set;

@Value
@Builder
@ValidDifferentCurrenciesCode(message = "Both currencies are the same!")
public class ExchangeRateRequestDto {

    @NotBlank(message = "Currency code is empty!")
    @Size(min = 3, max = 3, message = "Currency code length must be 3 symbols!")
    @ValidIso4217(message = "Currency code not in 4217 ISO!")
    String baseCurrency;

    @NotBlank(message = "Currency code is empty!")
    @Size(min = 3, max = 3, message = "Currency code length must be 3 symbols!")
    @ValidIso4217(message = "Currency code not in 4217 ISO!")
    String targetCurrency;

    @ValidNumberFormat(message = "Incorrect rate format!")
    String rate;

}
