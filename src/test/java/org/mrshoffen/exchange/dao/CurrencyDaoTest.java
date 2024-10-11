package org.mrshoffen.exchange.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyDaoTest {
    private static final CurrencyDao currencyDao = CurrencyDaoImpl.getInstance();

    private static Currency USD_currency_in_table;
    private static Currency RUB_currency_not_in_table;
    private static final int SIZE_OF_TEST_TABLE = 5;

    @BeforeEach
    void initTestData() {
        DatabaseManipulation.createAndFillTestTables();
        USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
    }

    @AfterEach
    void dropTestTables() {
        DatabaseManipulation.dropTestTables();
    }

    @Test
    void findAllCurrencies_Success_AfterTableCreation() {
        List<Currency> all = currencyDao.findAll();
        assertEquals(SIZE_OF_TEST_TABLE, all.size());
    }

    @Test
    void findCurrencyByCode_Success_IfCurrencyInTable() {
        Optional<Currency> maybeCurrency = currencyDao.findByCode(USD_currency_in_table.getCode());
        assertTrue(maybeCurrency.isPresent());
        maybeCurrency.ifPresent(currency -> assertEquals(USD_currency_in_table, currency));
    }

    @Test
    void findCurrencyByCode_Fail_IfCurrencyNotInTable() {
        Optional<Currency> maybeCurrency = currencyDao.findByCode(RUB_currency_not_in_table.getCode());
        assertTrue(maybeCurrency.isEmpty());
    }

    @Test
    void saveCurrency_Success_ifCurrencyNotInTable() {
        Optional<Currency> savedRub = currencyDao.save(RUB_currency_not_in_table);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedRub.isPresent());
        assertEquals(SIZE_OF_TEST_TABLE + 1, allCurrencies.size());
        assertEquals(6, savedRub.get().getId());
    }

    @Test
    void saveCurrency_Fail_ifCurrencyInTable() {
        Optional<Currency> savedCurrency = currencyDao.save(USD_currency_in_table);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedCurrency.isEmpty());
        assertEquals(SIZE_OF_TEST_TABLE, allCurrencies.size());
    }

}