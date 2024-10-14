package org.mrshoffen.exchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.entity.Currency;

@Mapper
public interface CurrencyMapper {

    @Named("currencyToDtoMethod")
    @Mapping(target = "name", source = "fullName")
    CurrencyResponseDto toDto(Currency currency);

    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "id", ignore = true)
    Currency toCurrency(CurrencyRequestDto requestDto);


}
