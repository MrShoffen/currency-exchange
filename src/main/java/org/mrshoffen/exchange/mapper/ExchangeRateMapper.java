package org.mrshoffen.exchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.entity.ExchangeRate;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {


    @Mapping(target = "baseCurrency", source = "baseCurrency", qualifiedByName = "currencyToDtoMethod")
    @Mapping(target = "targetCurrency", source = "targetCurrency", qualifiedByName = "currencyToDtoMethod")
    ExchangeRateResponseDto toDto(ExchangeRate object);
}
