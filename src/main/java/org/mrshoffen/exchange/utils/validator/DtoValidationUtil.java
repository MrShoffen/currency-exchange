package org.mrshoffen.exchange.utils.validator;

import lombok.experimental.UtilityClass;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DtoValidationUtil {
    private static final Set<String> ALL_CURRENCIES_CODES = Currency.getAvailableCurrencies().stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    public static ValResult validate(CurrencyRequestDto dto) {
        return ValResult.createEmpty()
                .and(validateCurrencyCode(dto.getCode()))
                .and(validateCurrencyName(dto.getName()))
                .and(validateCurrencySign(dto.getSign()));
    }

    public static ValResult validate(ExchangeRequestDto dto) {
        return ValResult.createEmpty()
                .and(validateBothCodes(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode()))
                .and(validateAmount(dto.getAmount()));
    }

    public static ValResult validate(ExchangeRateRequestDto dto) {
        return ValResult.createEmpty()
                .and(validateBothCodes(dto.getBaseCurrency(), dto.getTargetCurrency()))
                .and(validateRate(dto.getRate()));
    }

    public static ValResult validateCurrencyCode(String code) {
        if (code == null || code.length() != 3 || code.isBlank()) {
            return ValResult.of("Currency code must contain 3 symbols!");
        }

        if (!ALL_CURRENCIES_CODES.contains(code)) {
            return ValResult.of("No such currency " + code + " in ISO 4217 codes!");
        }
        return ValResult.createEmpty();
    }

    public static ValResult validateBothCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return ValResult.createEmpty()
                .and(validateSameCurrencies(baseCurrencyCode, targetCurrencyCode))
                .and(validateCurrencyCode(baseCurrencyCode))
                .and(validateCurrencyCode(targetCurrencyCode));
    }

    private static ValResult validateSameCurrencies(String baseCurrencyCode, String targetCurrencyCode) {
        return baseCurrencyCode.equals(targetCurrencyCode) ? ValResult.of("Both currencies are the same")
                : ValResult.createEmpty();
    }


    private static ValResult validateCurrencySign(String sign) {
        return sign == null || sign.isBlank() ? ValResult.of("Currency sign is empty!") : ValResult.createEmpty();
    }

    private static ValResult validateCurrencyName(String name) {
        return name == null || name.isBlank() ? ValResult.of("Currency name is empty!") : ValResult.createEmpty();
    }

    private static ValResult validateRate(String s) {
        try {
            BigDecimal value = new BigDecimal(s);
            return value.compareTo(BigDecimal.ZERO) > 0 ? ValResult.createEmpty() :
                    ValResult.of("Rate must be positive");
        } catch (Exception e) {
            return ValResult.of("Incorrect rate format!");
        }
    }

    private static ValResult validateAmount(String s) {
        try {
            BigDecimal value = new BigDecimal(s);
            return value.compareTo(BigDecimal.ZERO) > 0 ? ValResult.createEmpty() :
                    ValResult.of("Amount must be positive");
        } catch (Exception e) {
            return ValResult.of("Incorrect amount format!");
        }
    }


}
