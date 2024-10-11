package org.mrshoffen.exchange.utils.exchange.dao;

import org.mrshoffen.exchange.utils.exchange.entity.Currency;

import java.util.Optional;

public interface CurrencyDao extends Dao<Currency> {

    Optional<Currency> findByCode(String code);
}
