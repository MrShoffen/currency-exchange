package org.mrshoffen.exchange.dao;

import org.junit.jupiter.api.*;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrencyDaoTest {

    private static CurrencyDao currencyDao;


    @BeforeAll
    static void setUpTablesAndDependencies(){
        currencyDao = DependencyManager.getInjector().getInstance(CurrencyDao.class);
        DatabaseManipulation.createAndFillTestTables();
    }

    @AfterAll
    static void dropTables(){
        DatabaseManipulation.dropTestTables();
    }


    @Order(1)
    @Test
    void findAllCurrencies_Success_AfterTableCreation() {
        List<Currency> all = currencyDao.findAll();
        int expectedSize = 5;
        assertEquals(expectedSize, all.size());
    }

    @Order(2)
    @Test
    void findCurrencyByCode_Success_IfCurrencyInTable() {
        Currency USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();

        Optional<Currency> maybeCurrency = currencyDao.findByCode(USD_currency_in_table.getCode());
        assertTrue(maybeCurrency.isPresent());
        maybeCurrency.ifPresent(currency -> assertEquals(USD_currency_in_table, currency));
    }

    @Order(3)
    @Test
    void findCurrencyByCode_Fail_IfCurrencyNotInTable() {
        Currency RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();

        Optional<Currency> maybeCurrency = currencyDao.findByCode(RUB_currency_not_in_table.getCode());
        assertTrue(maybeCurrency.isEmpty());
    }

    @Order(4)
    @Test
    void saveCurrency_Success_ifCurrencyNotInTable() {
        Currency RUB_currency_for_save = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        int expectedSize = currencyDao.findAll().size() + 1;

        Optional<Currency> savedRub = currencyDao.save(RUB_currency_for_save);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedRub.isPresent());
        assertEquals(expectedSize, allCurrencies.size());
        assertEquals(6, savedRub.get().getId());
    }

    @Order(5)
    @Test
    void saveCurrency_Fail_ifCurrencyInTable() {
        Currency USD_currency_already_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        int expectedSize = currencyDao.findAll().size();


        Optional<Currency> savedCurrency = currencyDao.save(USD_currency_already_in_table);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedCurrency.isEmpty());
        assertEquals(expectedSize, allCurrencies.size());
    }

}