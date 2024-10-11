package org.mrshoffen.exchange.utils.exchange.utils.validator;

import lombok.experimental.UtilityClass;
import org.mrshoffen.exchange.utils.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.utils.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.utils.exchange.dto.request.ExchangeRequestDto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DtoValidationUtil {
    private static final Set<String> ALL_CURRENCIES_CODES = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    public static ValidationResult validate(CurrencyRequestDto dto) {
        return ValidationResult.createEmpty()
                .and(validateCurrencyCode(dto.getCode()))
                .and(validateCurrencyName(dto.getName()))
                .and(validateCurrencySign(dto.getSign()));
    }

    public static ValidationResult validate(ExchangeRequestDto dto) {
        return ValidationResult.createEmpty()
                .and(validateBothCodes(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode()))
                .and(validateAmount(dto.getAmount()));
    }

    public static ValidationResult validate(ExchangeRateRequestDto dto) {
        return ValidationResult.createEmpty()
                .and(validateBothCodes(dto.getBaseCurrency(), dto.getTargetCurrency()))
                .and(validateRate(dto.getRate()));
    }

    public static ValidationResult validateCurrencyCode(String code) {
        if (code == null || code.length() != 3 || code.isBlank()) {
            return ValidationResult.of("Currency code must contain 3 symbols!");
        }

        if (!ALL_CURRENCIES_CODES.contains(code)) {
            return ValidationResult.of("No such currency " + code + " in ISO 4217 codes!");
        }
        return ValidationResult.createEmpty();
    }

    public static ValidationResult validateBothCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return ValidationResult.createEmpty()
                .and(validateSameCurrencies(baseCurrencyCode, targetCurrencyCode))
                .and(validateCurrencyCode(baseCurrencyCode))
                .and(validateCurrencyCode(targetCurrencyCode));
    }

    private static ValidationResult validateSameCurrencies(String baseCurrencyCode, String targetCurrencyCode) {
        return baseCurrencyCode.equals(targetCurrencyCode) ? ValidationResult.of("Both currencies are the same")
                : ValidationResult.createEmpty();
    }


    private static ValidationResult validateCurrencySign(String sign) {
        return sign == null || sign.isBlank() ? ValidationResult.of("Currency sign is empty!") : ValidationResult.createEmpty();
    }

    private static ValidationResult validateCurrencyName(String name) {
        return name == null || name.isBlank() ? ValidationResult.of("Currency name is empty!") : ValidationResult.createEmpty();
    }

    private static ValidationResult validateRate(String s) {
        try {
            BigDecimal value = new BigDecimal(s);
            return value.compareTo(BigDecimal.ZERO) > 0 ? ValidationResult.createEmpty() :
                    ValidationResult.of("Rate must be positive");
        } catch (Exception e) {
            return ValidationResult.of("Incorrect rate format!");
        }
    }

    private static ValidationResult validateAmount(String s) {
        try {
            BigDecimal value = new BigDecimal(s);
            return value.compareTo(BigDecimal.ZERO) > 0 ? ValidationResult.createEmpty() :
                    ValidationResult.of("Amount must be positive");
        } catch (Exception e) {
            return ValidationResult.of("Incorrect amount format!");
        }
    }


}
