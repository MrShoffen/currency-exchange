package org.mrshoffen.exchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mrshoffen.exchange.dto.response.ExchangeResponseDto;
import org.mrshoffen.exchange.entity.ExchangeRate;

import java.math.BigDecimal;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeMapper {

    @Mapping(target = "baseCurrency", source = "entity.baseCurrency", qualifiedByName = "currencyToDtoMethod")
    @Mapping(target = "targetCurrency", source = "entity.targetCurrency", qualifiedByName = "currencyToDtoMethod")
    ExchangeResponseDto toDto(ExchangeRate entity, BigDecimal amount, BigDecimal convertedAmount);
}
