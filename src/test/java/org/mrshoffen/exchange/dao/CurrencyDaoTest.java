package org.mrshoffen.exchange.dao;

import jakarta.inject.Inject;
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

    private static Currency USD_currency_in_table;
    private static Currency RUB_currency_not_in_table;
    private static int SIZE_OF_TEST_TABLE;

    @BeforeAll
    static void injectDependencies(){
        currencyDao = new CurrencyDaoImpl();
        DatabaseManipulation.createAndFillTestTables();

        USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();

    }

    @AfterAll
    static void dropT(){
        DatabaseManipulation.dropTestTables();
    }

    @BeforeEach
    void initTestData() {
        SIZE_OF_TEST_TABLE = currencyDao.findAll().size();
    }


    @Order(1)
    @Test
    void findAllCurrencies_Success_AfterTableCreation() {
        List<Currency> all = currencyDao.findAll();
        assertEquals(SIZE_OF_TEST_TABLE, all.size());
    }

    @Order(2)
    @Test
    void findCurrencyByCode_Success_IfCurrencyInTable() {
        Optional<Currency> maybeCurrency = currencyDao.findByCode(USD_currency_in_table.getCode());
        assertTrue(maybeCurrency.isPresent());
        maybeCurrency.ifPresent(currency -> assertEquals(USD_currency_in_table, currency));
    }

    @Order(3)
    @Test
    void findCurrencyByCode_Fail_IfCurrencyNotInTable() {
        Optional<Currency> maybeCurrency = currencyDao.findByCode(RUB_currency_not_in_table.getCode());
        assertTrue(maybeCurrency.isEmpty());
    }

    @Order(4)
    @Test
    void saveCurrency_Success_ifCurrencyNotInTable() {
        Optional<Currency> savedRub = currencyDao.save(RUB_currency_not_in_table);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedRub.isPresent());
        assertEquals(SIZE_OF_TEST_TABLE + 1, allCurrencies.size());
        assertEquals(6, savedRub.get().getId());
    }

    @Order(5)
    @Test
    void saveCurrency_Fail_ifCurrencyInTable() {
        Optional<Currency> savedCurrency = currencyDao.save(USD_currency_in_table);
        List<Currency> allCurrencies = currencyDao.findAll();

        assertTrue(savedCurrency.isEmpty());
        assertEquals(SIZE_OF_TEST_TABLE, allCurrencies.size());
    }

}