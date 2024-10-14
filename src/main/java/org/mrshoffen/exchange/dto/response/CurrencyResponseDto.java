package org.mrshoffen.exchange.dto.response;

public record CurrencyResponseDto(
        Integer id,
        String name,
        String code,
        String sign) {
}
