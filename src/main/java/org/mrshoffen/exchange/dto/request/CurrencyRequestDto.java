package org.mrshoffen.exchange.dto.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyRequestDto {
    String code;
    String name;
    String sign;
}
