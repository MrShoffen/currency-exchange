package org.mrshoffen.exchange.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.MappingUtil;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeRateServiceTest {
    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    private static Currency USD_currency_in_table;
    private static Currency EUR_currency_in_table;
    private static Currency RUB_currency_not_in_table;
    private static Currency CNY_currency_in_table_without_exRate;

    private static ExchangeRateRequestDto USD_EUR_find_request;
    private static ExchangeRateResponseDto USD_EUR_expected_response;

    private static ExchangeRateRequestDto USD_RUB_find_request_not_in_table;

    private static ExchangeRateRequestDto INCORRECT_REQUEST_1;
    private static ExchangeRateRequestDto INCORRECT_REQUEST_2;

    private static ExchangeRateRequestDto USD_CNY_save_request;
    private static ExchangeRateResponseDto USD_CNY_expected_after_save;

    private static ExchangeRateRequestDto USD_EUR_update_request;
    private static ExchangeRateResponseDto USD_EUR_after_update_response;


    private static final int SIZE_OF_TEST_TABLE = 3;


    @BeforeEach
    void initTestData() {
        DatabaseManipulation.createAndFillTestTables();

        USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        EUR_currency_in_table = Currency.builder().id(2).code("EUR").fullName("Euro").sign("€").build();
        RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        CNY_currency_in_table_without_exRate = Currency.builder().id(4).code("CNY").fullName("Yuan Renminbi").sign("¥").build();


        USD_EUR_find_request = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(EUR_currency_in_table.getCode())
                .build();
        USD_EUR_expected_response = ExchangeRateResponseDto.builder()
                .id(1)
                .baseCurrency(MappingUtil.mapEntityToDto(USD_currency_in_table))
                .targetCurrency(MappingUtil.mapEntityToDto(EUR_currency_in_table))
                .rate(BigDecimal.valueOf(0.9))
                .build();

        USD_RUB_find_request_not_in_table = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(RUB_currency_not_in_table.getCode())
                .build();

        USD_CNY_save_request = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(CNY_currency_in_table_without_exRate.getCode())
                .rate("1.2")
                .build();
        USD_CNY_expected_after_save = ExchangeRateResponseDto.builder()
                .id(4)
                .baseCurrency(MappingUtil.mapEntityToDto(USD_currency_in_table))
                .targetCurrency(MappingUtil.mapEntityToDto(CNY_currency_in_table_without_exRate))
                .rate(BigDecimal.valueOf(1.2))
                .build();


        INCORRECT_REQUEST_1 = ExchangeRateRequestDto.builder().baseCurrency("USD").build();
        INCORRECT_REQUEST_2 = ExchangeRateRequestDto.builder().baseCurrency("ABCEUR").build();

        USD_EUR_update_request = ExchangeRateRequestDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate("0.5")
                .build();

        USD_EUR_after_update_response = ExchangeRateResponseDto.builder()
                .id(1)
                .baseCurrency(MappingUtil.mapEntityToDto(USD_currency_in_table))
                .targetCurrency(MappingUtil.mapEntityToDto(EUR_currency_in_table))
                .rate(BigDecimal.valueOf(0.5))
                .build();
    }

    @AfterEach
    void dropTestTables() {
        DatabaseManipulation.dropTestTables();
    }

    @Test
    void findAllExchangeRate_Success_AfterTableCreation() {
        List<ExchangeRateResponseDto> allExchangeRate = exchangeRateService.findAll();
        assertEquals(SIZE_OF_TEST_TABLE, allExchangeRate.size());
    }

    @Test
    void findExchangeRateByCodes_Success_IfExchangeRateInTable() {
        ExchangeRateResponseDto responseDto = exchangeRateService.findByCodes(USD_EUR_find_request);
        assertEquals(USD_EUR_expected_response, responseDto);
    }

    @Test
    void findExchangeRateByCode_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class, () -> exchangeRateService.findByCodes(USD_RUB_find_request_not_in_table));
    }

    @Test
    void findExchangeRateByCode_Throw_IfExchangeRateCodeIncorrect() {
        assertThrows(ValidationException.class, () -> exchangeRateService.findByCodes(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> exchangeRateService.findByCodes(INCORRECT_REQUEST_2));
    }

    @Test
    void saveExchangeRate_Success_IfExchangeRateNotInTable() {
        ExchangeRateResponseDto responseDto = exchangeRateService.save(USD_CNY_save_request);

        List<ExchangeRateResponseDto> allExchangeRates = exchangeRateService.findAll();

        assertEquals(USD_CNY_expected_after_save, responseDto);
        assertEquals(SIZE_OF_TEST_TABLE + 1, allExchangeRates.size());
    }

    @Test
    void saveExchangeRate_Throw_IfExchangeRateCodeIsIncorrect() {
        assertThrows(ValidationException.class, () -> exchangeRateService.save(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> exchangeRateService.save(INCORRECT_REQUEST_2));
    }

    @Test
    void saveExchangeRate_Throw_IfExchangeRateAlreadyInTable() {
        exchangeRateService.save(USD_CNY_save_request);

        List<ExchangeRateResponseDto> allExchangeRates = exchangeRateService.findAll();

        assertEquals(SIZE_OF_TEST_TABLE + 1, allExchangeRates.size());


        assertThrows(EntityAlreadyExistsException.class, () -> exchangeRateService.save(USD_CNY_save_request));
    }

    @Test
    void updateExchangeRate_Success_IfExchangeRateAlreadyInTable() {
        ExchangeRateResponseDto afterUpdate = exchangeRateService.update(USD_EUR_update_request);

        assertEquals(USD_EUR_after_update_response, afterUpdate);
    }

    @Test
    void updateExchangeRate_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class,
                () -> exchangeRateService.update(USD_CNY_save_request));
    }

    @Test
    void updateExchangeRate_Throw_IfExchangeRateCodeOrRateIsIncorrect() {
        assertThrows(ValidationException.class,
                () -> exchangeRateService.update(INCORRECT_REQUEST_1));

        assertThrows(ValidationException.class,
                () -> exchangeRateService.update(USD_RUB_find_request_not_in_table));
    }


}