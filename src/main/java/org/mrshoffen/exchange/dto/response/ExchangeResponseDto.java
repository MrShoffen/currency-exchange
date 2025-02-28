package org.mrshoffen.exchange.dto.response;

import java.math.BigDecimal;

public record ExchangeResponseDto(
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}
