package org.mrshoffen.exchange.service;


import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.CurrencyDaoImpl;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.utils.MappingUtil;
import org.mrshoffen.exchange.utils.validator.DtoValidationUtil;
import org.mrshoffen.exchange.utils.validator.ValResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CurrencyService {

    @Inject
    private CurrencyDao currencyDao;

//    @Inject
//    public CurrencyService(CurrencyDao currencyDao) {
//        this.currencyDao = currencyDao;
//    }


    public List<CurrencyResponseDto> findAll() {
        List<Currency> allCurrencies = currencyDao.findAll();

        return allCurrencies.stream().map(MappingUtil::mapEntityToDto).collect(Collectors.toList());
    }

    public CurrencyResponseDto findByCode(CurrencyRequestDto requestDto) {

        ValResult result = DtoValidationUtil.validateCurrencyCode(requestDto.getCode());
        if (result.isNotValid()) {
            throw new ValidationException(result.allValidatingErrors());
        }

        Optional<CurrencyResponseDto> currencyResponseDto = currencyDao.findByCode(requestDto.getCode())
                .map(MappingUtil::mapEntityToDto);

        return currencyResponseDto
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Error! Currency with code %s not found in database."
                                        .formatted(requestDto.getCode()))
                );
    }

    public CurrencyResponseDto saveCurrency(CurrencyRequestDto requestDto) {

        ValResult result = DtoValidationUtil.validate(requestDto);
        if (result.isNotValid()) {
            throw new ValidationException(result.allValidatingErrors());
        }

        Currency currencyForSave = MappingUtil.mapDtoToEntity(requestDto);
        Optional<CurrencyResponseDto> savedCurrency = currencyDao.save(currencyForSave)
                .map(MappingUtil::mapEntityToDto);

        return savedCurrency
                .orElseThrow(
                        () -> new EntityAlreadyExistsException(
                                "Error! Currency with code %s or name %s already exists in database."
                                .formatted(currencyForSave.getCode(), currencyForSave.getFullName()))
                );

    }
}
