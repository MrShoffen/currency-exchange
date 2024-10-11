package org.mrshoffen.exchange.service;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.mrshoffen.exchange.dao.ExchangeRateDao;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeResponseDto;
import org.mrshoffen.exchange.entity.ExchangeRate;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.utils.MappingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;

import static java.math.MathContext.DECIMAL64;

public class ExchangeService {

    @Inject
    private ExchangeRateDao exchangeRateDao;

    @Inject
    private Validator validator;


    public ExchangeResponseDto exchange(ExchangeRequestDto requestDto) {

        Set<ConstraintViolation<ExchangeRequestDto>> validationErrors = validator.validate(requestDto);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        ExchangeRate exchangeRate = findExchangeRate(requestDto.getBaseCurrencyCode(), requestDto.getTargetCurrencyCode())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Exchange rate from %s to %s not found."
                                        .formatted(requestDto.getBaseCurrencyCode(), requestDto.getTargetCurrencyCode()))
                );

        BigDecimal amount = new BigDecimal(requestDto.getAmount());
        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate())
                .setScale(2, RoundingMode.HALF_EVEN);

        return ExchangeResponseDto.builder()
                .baseCurrency(MappingUtil.mapEntityToDto(exchangeRate.getBaseCurrency()))
                .targetCurrency(MappingUtil.mapEntityToDto(exchangeRate.getTargetCurrency()))
                .amount(amount)
                .convertedAmount(convertedAmount)
                .rate(exchangeRate.getRate())
                .build();
    }

    private Optional<ExchangeRate> findExchangeRate(String baseC, String targetC) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(baseC, targetC);

        if (exchangeRate.isEmpty()) {
            exchangeRate = findReverseRate(baseC, targetC);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = findCrossRate(baseC, targetC);
        }

        return exchangeRate;
    }

    private Optional<ExchangeRate> findReverseRate(String baseC, String targetC) {
        Optional<ExchangeRate> maybeReverseRate = exchangeRateDao.findByCodes(targetC, baseC);

        if (maybeReverseRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate reverseExRate = maybeReverseRate.get();
        BigDecimal reversedRate = BigDecimal.ONE.divide(reverseExRate.getRate(), DECIMAL64)
                .setScale(6, RoundingMode.HALF_EVEN);

        return Optional.of(
                ExchangeRate.builder()
                        .baseCurrency(reverseExRate.getTargetCurrency())
                        .targetCurrency(reverseExRate.getBaseCurrency())
                        .rate(reversedRate)
                        .build()
        );
    }

    private Optional<ExchangeRate> findCrossRate(String baseC, String targetC) {
        Optional<ExchangeRate> maybeUsdToBaseRate = exchangeRateDao.findByCodes("USD", baseC);

        Optional<ExchangeRate> maybeUsdToTargetRate = exchangeRateDao.findByCodes("USD", targetC);

        if (maybeUsdToBaseRate.isEmpty() || maybeUsdToTargetRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate usdToBase = maybeUsdToBaseRate.get();
        ExchangeRate usdToTarget = maybeUsdToTargetRate.get();

        BigDecimal newRate = usdToTarget.getRate().divide(usdToBase.getRate(), DECIMAL64)
                .setScale(6, RoundingMode.HALF_EVEN);

        return Optional.of(
                ExchangeRate.builder()
                        .baseCurrency(usdToBase.getTargetCurrency())
                        .targetCurrency(usdToTarget.getTargetCurrency())
                        .rate(newRate)
                        .build()
        );
    }
}
