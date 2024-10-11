package org.mrshoffen.exchange.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExchangeRateRequestDto {
    String baseCurrency;
    String targetCurrency;
    String rate;
}
