package org.mrshoffen.exchange.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.CurrencyDaoImpl;
import org.mrshoffen.exchange.dao.ExchangeRateDao;
import org.mrshoffen.exchange.dao.ExchangeRateDaoImpl;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.entity.ExchangeRate;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.utils.MappingUtil;
import org.mrshoffen.exchange.utils.validator.DtoValidationUtil;
import org.mrshoffen.exchange.utils.validator.ValResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDaoImpl.getInstance();
    private final CurrencyDao currencyDao = CurrencyDaoImpl.getInstance();


    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateResponseDto> findAll() {
        List<ExchangeRate> allExchangeRates = exchangeRateDao.findAll();

        return allExchangeRates.stream().map(MappingUtil::mapEntityToDto).collect(Collectors.toList());
    }

    public ExchangeRateResponseDto findByCodes(ExchangeRateRequestDto requestDto) {

        ValResult result = DtoValidationUtil.validateBothCodes(requestDto.getBaseCurrency(),requestDto.getTargetCurrency());
        if (result.isNotValid()) {
            throw new ValidationException(result.allValidatingErrors());
        }

        Optional<ExchangeRateResponseDto> exchangeRateResponseDto = exchangeRateDao
                .findByCodes(requestDto.getBaseCurrency(), requestDto.getTargetCurrency())
                .map(MappingUtil::mapEntityToDto);

        return exchangeRateResponseDto
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Exchange rate from %s to %s not found."
                                        .formatted(requestDto.getBaseCurrency(), requestDto.getTargetCurrency()))
                );
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto requestDto) {

        ValResult validationResult = DtoValidationUtil.validate(requestDto);
        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.allValidatingErrors());
        }

        ExchangeRate rateForSave = extractFullExchangeRate(requestDto);

        Optional<ExchangeRateResponseDto> exchangeRateAfterSave = exchangeRateDao.save(rateForSave)
                .map(MappingUtil::mapEntityToDto);

        return exchangeRateAfterSave
                .orElseThrow(
                        () -> new EntityAlreadyExistsException(
                                "Error! Exchange rate from %s to %s already exists!"
                                        .formatted(requestDto.getBaseCurrency(), requestDto.getTargetCurrency()))
                );
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto requestDto) {
        ValResult validationResult = DtoValidationUtil.validate(requestDto);

        if (validationResult.isNotValid()) {
            throw new ValidationException(validationResult.allValidatingErrors());
        }

        ExchangeRate rateForUpdate = extractFullExchangeRate(requestDto);

        Optional<ExchangeRateResponseDto> exchangeRateAfterUpdate = exchangeRateDao.update(rateForUpdate)
                .map(MappingUtil::mapEntityToDto);

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
