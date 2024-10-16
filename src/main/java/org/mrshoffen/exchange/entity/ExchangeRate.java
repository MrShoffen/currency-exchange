package org.mrshoffen.exchange.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeRate {
    Integer id;
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;
}
