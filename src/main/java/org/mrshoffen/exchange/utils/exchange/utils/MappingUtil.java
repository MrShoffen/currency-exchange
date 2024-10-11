package org.mrshoffen.exchange.utils.exchange.utils;

import lombok.experimental.UtilityClass;
import org.mrshoffen.exchange.utils.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.utils.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.utils.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.utils.exchange.entity.Currency;
import org.mrshoffen.exchange.utils.exchange.entity.ExchangeRate;

@UtilityClass
public class MappingUtil {

    public static CurrencyResponseDto mapEntityToDto(Currency object) {
        return CurrencyResponseDto.builder()
                .id(object.getId())
                .code(object.getCode())
                .name(object.getFullName())
                .sign(object.getSign())
                .build();
    }

    public static Currency mapDtoToEntity(CurrencyRequestDto dto) {
        return Currency.builder()
                .fullName(dto.getName())
                .sign(dto.getSign())
                .code(dto.getCode()).build();
    }

    public static ExchangeRateResponseDto mapEntityToDto(ExchangeRate object) {
        return ExchangeRateResponseDto.builder()
                .id(object.getId())
                .baseCurrency(mapEntityToDto(object.getBaseCurrency()))
                .targetCurrency(mapEntityToDto(object.getTargetCurrency()))
                .rate(object.getRate())
                .build();
    }
}
