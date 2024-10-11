package org.mrshoffen.exchange.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeResponseDto {
    CurrencyResponseDto baseCurrency;
    CurrencyResponseDto targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}
