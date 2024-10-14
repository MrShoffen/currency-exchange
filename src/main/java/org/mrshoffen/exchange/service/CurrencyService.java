package org.mrshoffen.exchange.service;


import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.mapper.CurrencyMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyService {

    @Inject
    private CurrencyDao currencyDao;

    @Inject
    private Validator validator;

    @Inject
    private CurrencyMapper currencyMapper;


    public List<CurrencyResponseDto> findAll() {
        List<Currency> allCurrencies = currencyDao.findAll();

        return allCurrencies.stream().map(currencyMapper::toDto).collect(Collectors.toList());
    }

    public CurrencyResponseDto findByCode(CurrencyRequestDto requestDto) {

        Set<ConstraintViolation<CurrencyRequestDto>> validationErrors = validator.validateProperty(requestDto, "code");

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        Optional<CurrencyResponseDto> currencyResponseDto = currencyDao.findByCode(requestDto.getCode())
                .map(currencyMapper::toDto);

        return currencyResponseDto
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Currency with code %s not found in database."
                                        .formatted(requestDto.getCode()))
                );
    }

    public CurrencyResponseDto saveCurrency(CurrencyRequestDto requestDto) {

        Set<ConstraintViolation<CurrencyRequestDto>> validationErrors = validator.validate(requestDto);

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

        Currency currencyForSave = currencyMapper.toCurrency(requestDto);
        Optional<CurrencyResponseDto> savedCurrency = currencyDao.save(currencyForSave)
                .map(currencyMapper::toDto);

        return savedCurrency
                .orElseThrow(
                        () -> new EntityAlreadyExistsException(
                                "Error! Currency with code %s or name %s already exists in database."
                                .formatted(currencyForSave.getCode(), currencyForSave.getFullName()))
                );

    }
}
