package org.mrshoffen.exchange.utils.exchange.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyResponseDto {
    Integer id;
    String name;
    String code;
    String sign;
}
