package org.mrshoffen.exchange.service;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.ExchangeRateDao;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.entity.ExchangeRate;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.mapper.ExchangeRateMapper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExchangeRateService {

    @Inject
    private ExchangeRateDao exchangeRateDao;

    @Inject
    private CurrencyDao currencyDao;

    @Inject
    private Validator validator;

    @Inject
    private ExchangeRateMapper exchangeRateMapper;


    public List<ExchangeRateResponseDto> findAll() {
        List<ExchangeRate> allExchangeRates = exchangeRateDao.findAll();

        return allExchangeRates.stream().map(exchangeRateMapper::toDto).collect(Collectors.toList());
    }

    public ExchangeRateResponseDto findByCodes(ExchangeRateRequestDto requestDto) {
        Set<ConstraintViolation<ExchangeRateRequestDto>> baseCurrencyErr = validator.validateProperty(requestDto, "baseCurrency");
        Set<ConstraintViolation<ExchangeRateRequestDto>> targetCurrencyErr = validator.validateProperty(requestDto, "targetCurrency");

        Set<ConstraintViolation<ExchangeRateRequestDto>> validationResult = new HashSet<>(baseCurrencyErr);
        validationResult.addAll(targetCurrencyErr);
        if (!validationResult.isEmpty()) {
            throw new ValidationException(baseCurrencyErr);
        }

        Optional<ExchangeRateResponseDto> exchangeRateResponseDto = exchangeRateDao
                .findByCodes(requestDto.getBaseCurrency(), requestDto.getTargetCurrency())
                .map(exchangeRateMapper::toDto);

        return exchangeRateResponseDto
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Exchange rate from %s to %s not found."
                                        .formatted(requestDto.getBaseCurrency(), requestDto.getTargetCurrency()))
                );
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto requestDto) {

        Set<ConstraintViolation<ExchangeRateRequestDto>> validationErrors = validator.validate(requestDto);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        ExchangeRate rateForSave = extractFullExchangeRate(requestDto);

        Optional<ExchangeRateResponseDto> exchangeRateAfterSave = exchangeRateDao.save(rateForSave)
                .map(exchangeRateMapper::toDto);

        return exchangeRateAfterSave
                .orElseThrow(
                        () -> new EntityAlreadyExistsException(
                                "Error! Exchange rate from %s to %s already exists!"
                                        .formatted(requestDto.getBaseCurrency(), requestDto.getTargetCurrency()))
                );
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto requestDto) {

        Set<ConstraintViolation<ExchangeRateRequestDto>> validationErrors = validator.validate(requestDto);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        ExchangeRate rateForUpdate = extractFullExchangeRate(requestDto);

        Optional<ExchangeRateResponseDto> exchangeRateAfterUpdate = exchangeRateDao.update(rateForUpdate)
                .map(exchangeRateMapper::toDto);

        return exchangeRateAfterUpdate
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Exchange rate from %s to %s not found."
                                        .formatted(requestDto.getBaseCurrency(), requestDto.getTargetCurrency()))
                );
    }

    private ExchangeRate extractFullExchangeRate(ExchangeRateRequestDto requestDto) {
        Currency baseCurrency = getCurrencyFromDB(requestDto.getBaseCurrency());
        Currency targetCurrency = getCurrencyFromDB(requestDto.getTargetCurrency());

        return ExchangeRate.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(new BigDecimal(requestDto.getRate()))
                .build();
    }

    private Currency getCurrencyFromDB(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Currency with code %s  not found"
                                .formatted(code))
                );
    }
}
