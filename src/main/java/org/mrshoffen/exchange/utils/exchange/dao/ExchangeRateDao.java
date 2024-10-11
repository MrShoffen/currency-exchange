package org.mrshoffen.exchange.utils.exchange.dao;

import org.mrshoffen.exchange.utils.exchange.entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends Dao<ExchangeRate> {

    Optional<ExchangeRate> findByCodes(String baseCurrency, String targetCurrency);

    Optional<ExchangeRate> update(ExchangeRate entity);
}
