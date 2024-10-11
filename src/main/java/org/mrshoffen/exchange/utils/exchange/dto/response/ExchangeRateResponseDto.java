package org.mrshoffen.exchange.utils.exchange.dto.response;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeRateResponseDto {
    Integer id;
    CurrencyResponseDto baseCurrency;
    CurrencyResponseDto targetCurrency;
    BigDecimal rate;
}
