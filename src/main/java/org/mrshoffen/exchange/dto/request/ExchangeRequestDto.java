package org.mrshoffen.exchange.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExchangeRequestDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
    String amount;
}
