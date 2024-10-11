package org.mrshoffen.exchange.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CurrencyServiceTest {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    private static CurrencyRequestDto USD_find_request;
    private static CurrencyResponseDto USD_expected_response;

    private static CurrencyRequestDto RUB_find_request;
    private static CurrencyResponseDto RUB_expected_response;

    private static CurrencyRequestDto INCORRECT_REQUEST_1;
    private static CurrencyRequestDto INCORRECT_REQUEST_2;

    private static CurrencyRequestDto RUB_save_request;

    private static final int SIZE_OF_TEST_TABLE = 5;

    @BeforeEach
    void initTestData() {
        DatabaseManipulation.createAndFillTestTables();
        USD_find_request = CurrencyRequestDto.builder().code("USD").build();
        USD_expected_response = CurrencyResponseDto.builder().id(1).code("USD").name("US Dollar").sign("$").build();
        RUB_find_request = CurrencyRequestDto.builder().code("RUB").build();
        INCORRECT_REQUEST_1 = CurrencyRequestDto.builder().code("A").build();
        INCORRECT_REQUEST_2 = CurrencyRequestDto.builder().code("ABC").build();

        RUB_save_request = CurrencyRequestDto.builder().code("RUB").name("Russian Ruble").sign("₽").build();
        RUB_expected_response = CurrencyResponseDto.builder().id(6).code("RUB").name("Russian Ruble").sign("₽").build();

    }

    @AfterEach
    void dropTestTables() {
        DatabaseManipulation.dropTestTables();
    }


    @Test
    void findAllCurrencies_Success_AfterTableCreation(){
        List<CurrencyResponseDto> allCurrencies = currencyService.findAll();
        assertEquals(SIZE_OF_TEST_TABLE,allCurrencies.size());
    }

    @Test
    void findCurrencyByCode_Success_IfCurrencyInTable(){
        CurrencyResponseDto currencyResponseDto = currencyService.findByCode(USD_find_request);
        assertEquals(USD_expected_response,currencyResponseDto);
    }

    @Test
    void findCurrencyByCode_Throw_IfCurrencyNotInTable(){
        assertThrows(EntityNotFoundException.class, () -> currencyService.findByCode(RUB_find_request));
    }

    @Test
    void findCurrencyByCode_Throw_IfCurrencyCodeIncorrect(){
        assertThrows(ValidationException.class, () -> currencyService.findByCode(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> currencyService.findByCode(INCORRECT_REQUEST_2));
    }

    @Test
    void saveCurrency_Success_IfCurrencyNotInTable(){
        CurrencyResponseDto responseDto = currencyService.saveCurrency(RUB_save_request);

        assertEquals(RUB_expected_response,responseDto);
    }

    @Test
    void saveCurrency_Throw_IfCurrencyNameOrSignEmpty(){
        assertThrows(ValidationException.class, () -> currencyService.saveCurrency(INCORRECT_REQUEST_1));
    }

    @Test
    void saveCurrency_Throw_IfCurrencyAlreadyInTable(){
        currencyService.saveCurrency(RUB_save_request);

        assertThrows(EntityAlreadyExistsException.class, () ->currencyService.saveCurrency(RUB_save_request));
    }


}