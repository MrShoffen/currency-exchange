package org.mrshoffen.exchange.dao;

import org.junit.jupiter.api.*;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.entity.ExchangeRate;
import org.mrshoffen.exchange.service.CurrencyService;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeRateDaoTest {
    private static  ExchangeRateDao exchangeDao = DependencyManager.getInjector().getInstance(ExchangeRateDao.class);;
    private static  CurrencyDao currencyDao = DependencyManager.getInjector().getInstance(CurrencyDao.class);;

    private static Currency USD_currency_in_table;
    private static Currency EUR_currency_in_table;
    private static Currency CNY_currency_in_table_without_exRate;
    private static Currency RUB_currency_not_in_table;


    private static ExchangeRate USD_EUR_exchange_rate_in_table;
    private static ExchangeRate USD_RUB_exchange_not_in_table;

    @BeforeAll
    static void setUpTablesAndDependencies() {
        exchangeDao = DependencyManager.getInjector().getInstance(ExchangeRateDao.class);
        currencyDao = DependencyManager.getInjector().getInstance(CurrencyDao.class);
        DatabaseManipulation.createAndFillTestTables();


        USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        EUR_currency_in_table = Currency.builder().id(2).code("EUR").fullName("Euro").sign("€").build();
        CNY_currency_in_table_without_exRate = Currency.builder().id(4).code("CNY").fullName("Yuan Renminbi").sign("¥").build();

        RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        currencyDao.save(RUB_currency_not_in_table);

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



    @AfterAll
    static void dropTables(){
        DatabaseManipulation.dropTestTables();
    }


    @Order(1)
    @Test
    void findAllExchangeRates_Success_AfterTableCreation() {
        int expectedSize = 3;

        List<ExchangeRate> all = exchangeDao.findAll();
        assertEquals(expectedSize, all.size());
    }

    @Order(2)
    @Test
    void findExchangeRateByCodes_Success_IfExchangeRateInTable() {
        Optional<ExchangeRate> maybeExchangeRate =
                exchangeDao.findByCodes(USD_currency_in_table.getCode(), EUR_currency_in_table.getCode());
        assertTrue(maybeExchangeRate.isPresent());
        maybeExchangeRate.ifPresent(exchRate -> assertEquals(USD_EUR_exchange_rate_in_table, exchRate));
    }

    @Order(3)
    @Test
    void findExchangeRateByCodes_Fail_IfExchangeRateNotInTable() {
        Optional<ExchangeRate> maybeExchangeRate = exchangeDao.findByCodes(RUB_currency_not_in_table.getCode(), USD_currency_in_table.getCode());
        assertTrue(maybeExchangeRate.isEmpty());
    }

    @Order(4)
    @Test
    void findExchangeRateByCodes_Fail_IfCurrencyInTableButNoSuchRate() {
        Optional<ExchangeRate> maybeExchangeRate
                = exchangeDao.findByCodes(USD_currency_in_table.getCode(), CNY_currency_in_table_without_exRate.getCode());
        assertTrue(maybeExchangeRate.isEmpty());
    }

    @Order(5)
    @Test
    void updateExchangeRate_Fail_ifExchangeRateNotInTable() {
        USD_RUB_exchange_not_in_table.setRate(BigDecimal.valueOf(120.21));
        Optional<ExchangeRate> maybeUpdatedExchangeRate = exchangeDao.update(USD_RUB_exchange_not_in_table);

        assertTrue(maybeUpdatedExchangeRate.isEmpty());
    }

    @Order(6)
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

    @Order(7)
    @Test
    void saveExchangeRate_Success_ifExchangeRateNotInTable() {

        int expectedSize  = exchangeDao.findAll().size() + 1;

        Optional<ExchangeRate> savedUsdToRub = exchangeDao.save(USD_RUB_exchange_not_in_table);

        List<ExchangeRate> allCurrencies = exchangeDao.findAll();

        assertTrue(savedUsdToRub.isPresent());
        assertEquals(expectedSize, allCurrencies.size());
        assertEquals(4, savedUsdToRub.get().getId());
    }

    @Order(8)
    @Test
    void saveExchangeRate_Fail_ifExchangeRateInTable() {

        int expectedSize  = exchangeDao.findAll().size();

        Optional<ExchangeRate> savedUsdToRub = exchangeDao.save(USD_EUR_exchange_rate_in_table);


        List<ExchangeRate> allCurrencies = exchangeDao.findAll();

        assertTrue(savedUsdToRub.isEmpty());
        assertEquals(expectedSize, allCurrencies.size());
    }





}