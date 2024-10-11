package org.mrshoffen.exchange.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.entity.ExchangeRate;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRateDaoTest {
    private static final ExchangeRateDao exchangeDao = ExchangeRateDaoImpl.getInstance();
    private static final CurrencyDao currencyDao = CurrencyDaoImpl.getInstance();

    private static ExchangeRate USD_EUR_exchange_rate_in_table;
    private static Currency USD_currency_in_table;
    private static Currency EUR_currency_in_table;

    private static Currency CNY_currency_in_table_without_exRate;

    private static ExchangeRate USD_RUB_exchange_not_in_table;
    private static Currency RUB_currency_not_in_table;

    private static final int SIZE_OF_TEST_TABLE = 3;

    @BeforeEach
    void initTestData() {
        DatabaseManipulation.createAndFillTestTables();
        USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        EUR_currency_in_table = Currency.builder().id(2).code("EUR").fullName("Euro").sign("€").build();
        RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        currencyDao.save(RUB_currency_not_in_table);
        CNY_currency_in_table_without_exRate = Currency.builder().id(4).code("CNY").fullName("Yuan Renminbi").sign("¥").build();
        USD_EUR_exchange_rate_in_table = ExchangeRate.builder()
                .id(1)
                .rate(BigDecimal.valueOf(0.9))
                .baseCurrency(USD_currency_in_table)
                .targetCurrency(EUR_currency_in_table)
                .build();
        USD_RUB_exchange_not_in_table = ExchangeRate.builder()
                .baseCurrency(USD_currency_in_table)
                .targetCurrency(RUB_currency_not_in_table)
                .rate(BigDecimal.valueOf(91.2))
                .build();
    }

    @AfterEach
    void dropTestTables() {
        DatabaseManipulation.dropTestTables();
    }

    @Test
    void findAllExchangeRates_Success_AfterTableCreation() {
        List<ExchangeRate> all = exchangeDao.findAll();
        assertEquals(SIZE_OF_TEST_TABLE, all.size());
        assertTrue(all.contains(USD_EUR_exchange_rate_in_table));
    }

    @Test
    void findExchangeRateByCodes_Success_IfExchangeRateInTable() {
        Optional<ExchangeRate> maybeExchangeRate =
                exchangeDao.findByCodes(USD_currency_in_table.getCode(), EUR_currency_in_table.getCode());
        assertTrue(maybeExchangeRate.isPresent());
        maybeExchangeRate.ifPresent(exchRate -> assertEquals(USD_EUR_exchange_rate_in_table, exchRate));
    }

    @Test
    void findExchangeRateByCodes_Fail_IfExchangeRateNotInTable() {
        Optional<ExchangeRate> maybeExchangeRate = exchangeDao.findByCodes(RUB_currency_not_in_table.getCode(), USD_currency_in_table.getCode());
        assertTrue(maybeExchangeRate.isEmpty());
    }

    @Test
    void findExchangeRateByCodes_Fail_IfCurrencyInTableButNoSuchRate() {
        Optional<ExchangeRate> maybeExchangeRate
                = exchangeDao.findByCodes(USD_currency_in_table.getCode(), CNY_currency_in_table_without_exRate.getCode());
        assertTrue(maybeExchangeRate.isEmpty());
    }


    @Test
    void saveExchangeRate_Success_ifExchangeRateNotInTable() {
        Optional<ExchangeRate> savedUsdToRub = exchangeDao.save(USD_RUB_exchange_not_in_table);

        List<ExchangeRate> allCurrencies = exchangeDao.findAll();

        assertTrue(savedUsdToRub.isPresent());
        assertEquals(SIZE_OF_TEST_TABLE + 1, allCurrencies.size());
        assertEquals(4, savedUsdToRub.get().getId());
    }

    @Test
    void saveExchangeRate_Fail_ifExchangeRateInTable() {
        Optional<ExchangeRate> savedUsdToRub = exchangeDao.save(USD_EUR_exchange_rate_in_table);

        List<ExchangeRate> allCurrencies = exchangeDao.findAll();

        assertTrue(savedUsdToRub.isEmpty());
        assertEquals(SIZE_OF_TEST_TABLE, allCurrencies.size());
    }

    @Test
    void updateExchangeRate_Success_ifExchangeRateInTable() {
        USD_EUR_exchange_rate_in_table.setRate(BigDecimal.valueOf(0.8));
        Optional<ExchangeRate> maybeUpdatedExchangeRate = exchangeDao.update(USD_EUR_exchange_rate_in_table);

        assertTrue(maybeUpdatedExchangeRate.isPresent());

        Optional<ExchangeRate> maybeExchangeRate = exchangeDao.findByCodes(
                USD_currency_in_table.getCode(),
                EUR_currency_in_table.getCode()
        );
        assertTrue(maybeExchangeRate.isPresent());

        maybeExchangeRate.ifPresent(
                exchangeRate -> assertEquals(USD_EUR_exchange_rate_in_table.getRate(), exchangeRate.getRate()));

    }

    @Test
    void updateExchangeRate_Fail_ifExchangeRateNotInTable() {
        USD_RUB_exchange_not_in_table.setRate(BigDecimal.valueOf(120.21));
        Optional<ExchangeRate> maybeUpdatedExchangeRate = exchangeDao.update(USD_RUB_exchange_not_in_table);

        assertTrue(maybeUpdatedExchangeRate.isEmpty());
    }

}