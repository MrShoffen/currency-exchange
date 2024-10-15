package org.mrshoffen.exchange.service;

import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.ExchangeRateDao;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrencyServiceTest {


    private static CurrencyService currencyService;

    private static CurrencyRequestDto USD_find_request;
    private static CurrencyResponseDto USD_expected_response;

    private static CurrencyRequestDto RUB_find_request;
    private static CurrencyResponseDto RUB_expected_response;

    private static CurrencyRequestDto INCORRECT_REQUEST_1;
    private static CurrencyRequestDto INCORRECT_REQUEST_2;

    private static CurrencyRequestDto RUB_save_request;
    private static CurrencyRequestDto  USD_save_request;


    @BeforeAll
    static void setUpTablesAndDependencies() {
        currencyService = DependencyManager.getInjector().getInstance(CurrencyService.class);
        DatabaseManipulation.createAndFillTestTables();

        USD_find_request = CurrencyRequestDto.builder().code("USD").build();
        USD_expected_response = new CurrencyResponseDto(1, "US Dollar", "USD", "$");

        RUB_find_request = CurrencyRequestDto.builder().code("RUB").build();

        INCORRECT_REQUEST_1 = CurrencyRequestDto.builder().code("A").build();
        INCORRECT_REQUEST_2 = CurrencyRequestDto.builder().code("ABC").build();

        RUB_save_request = CurrencyRequestDto.builder().code("RUB").name("Russian Ruble").sign("₽").build();
        RUB_expected_response = new CurrencyResponseDto(6, "Russian Ruble", "RUB", "₽");

        USD_save_request = CurrencyRequestDto.builder().code("USD").name("US Dollar").sign("$").build();
    }

    @AfterAll
    static void dropTables(){
        DatabaseManipulation.dropTestTables();
    }


    @Order(1)
    @Test
    void findAllCurrencies_Success_AfterTableCreation() {
        int expectedSize = 5;

        List<CurrencyResponseDto> allCurrencies = currencyService.findAll();
        assertEquals(expectedSize, allCurrencies.size());
    }

    @Order(2)
    @Test
    void findCurrencyByCode_Success_IfCurrencyInTable() {
        CurrencyResponseDto currencyResponseDto = currencyService.findByCode(USD_find_request);
        assertEquals(USD_expected_response, currencyResponseDto);
    }

    @Order(3)
    @Test
    void findCurrencyByCode_Throw_IfCurrencyNotInTable() {
        assertThrows(EntityNotFoundException.class, () -> currencyService.findByCode(RUB_find_request));
    }

    @Order(4)
    @Test
    void findCurrencyByCode_Throw_IfCurrencyCodeIncorrect() {
        assertThrows(ValidationException.class, () -> currencyService.findByCode(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> currencyService.findByCode(INCORRECT_REQUEST_2));
    }

    @Order(5)
    @Test
    void saveCurrency_Success_IfCurrencyNotInTable() {
        CurrencyResponseDto responseDto = currencyService.saveCurrency(RUB_save_request);

        assertEquals(RUB_expected_response, responseDto);
    }

    @Order(6)
    @Test
    void saveCurrency_Throw_IfCurrencyNameOrSignEmpty() {
        assertThrows(ValidationException.class, () -> currencyService.saveCurrency(INCORRECT_REQUEST_1));
    }

    @Order(7)
    @Test
    void saveCurrency_Throw_IfCurrencyAlreadyInTable() {

        assertThrows(EntityAlreadyExistsException.class, () -> currencyService.saveCurrency(USD_save_request));
    }


}