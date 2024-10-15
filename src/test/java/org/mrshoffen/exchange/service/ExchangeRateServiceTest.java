package org.mrshoffen.exchange.service;

import org.junit.jupiter.api.*;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.mapper.CurrencyMapper;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeRateServiceTest {
    private static ExchangeRateService exchangeRateService;

    private static ExchangeRateRequestDto USD_EUR_find_request;
    private static ExchangeRateResponseDto USD_EUR_expected_response;

    private static ExchangeRateRequestDto USD_RUB_find_request_not_in_table;

    private static ExchangeRateRequestDto INCORRECT_REQUEST_1;
    private static ExchangeRateRequestDto INCORRECT_REQUEST_2;

    private static ExchangeRateRequestDto USD_CNY_save_request;
    private static ExchangeRateResponseDto USD_CNY_expected_after_save;
    private static ExchangeRateRequestDto USD_EUR_save_request;

    private static ExchangeRateRequestDto USD_EUR_update_request;
    private static ExchangeRateResponseDto USD_EUR_after_update_response;


    @BeforeAll
    static void setUpTablesAndDependencies() {
        exchangeRateService = DependencyManager.getInjector().getInstance(ExchangeRateService.class);
        CurrencyMapper currencyMapper = DependencyManager.getInjector().getInstance(CurrencyMapper.class);
        DatabaseManipulation.createAndFillTestTables();

        Currency USD_currency_in_table = Currency.builder().id(1).code("USD").fullName("US Dollar").sign("$").build();
        Currency EUR_currency_in_table = Currency.builder().id(2).code("EUR").fullName("Euro").sign("€").build();
        Currency RUB_currency_not_in_table = Currency.builder().code("RUB").fullName("Russian ruble").sign("₽").build();
        Currency CNY_currency_in_table_without_exRate = Currency.builder().id(4).code("CNY").fullName("Yuan Renminbi").sign("¥").build();


        USD_EUR_find_request = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(EUR_currency_in_table.getCode())
                .build();
        USD_EUR_expected_response = new ExchangeRateResponseDto(1, currencyMapper.toDto(USD_currency_in_table), currencyMapper.toDto(EUR_currency_in_table), BigDecimal.valueOf(0.9));

        USD_RUB_find_request_not_in_table = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(RUB_currency_not_in_table.getCode())
                .build();

        USD_CNY_save_request = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(CNY_currency_in_table_without_exRate.getCode())
                .rate("1.2")
                .build();
        USD_CNY_expected_after_save = new ExchangeRateResponseDto(4, currencyMapper.toDto(USD_currency_in_table), currencyMapper.toDto(CNY_currency_in_table_without_exRate), BigDecimal.valueOf(1.2));
        USD_EUR_save_request = ExchangeRateRequestDto.builder()
                .baseCurrency(USD_currency_in_table.getCode())
                .targetCurrency(EUR_currency_in_table.getCode())
                .rate("0.9")
                .build();

        INCORRECT_REQUEST_1 = ExchangeRateRequestDto.builder().baseCurrency("USD").build();
        INCORRECT_REQUEST_2 = ExchangeRateRequestDto.builder().baseCurrency("ABCEUR").build();

        USD_EUR_update_request = ExchangeRateRequestDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate("0.5")
                .build();
        USD_EUR_after_update_response = new ExchangeRateResponseDto(1, currencyMapper.toDto(USD_currency_in_table), currencyMapper.toDto(EUR_currency_in_table), BigDecimal.valueOf(0.5));

    }


    @AfterAll
    static void dropTables() {
        DatabaseManipulation.dropTestTables();
    }


    @Order(1)
    @Test
    void findAllExchangeRate_Success_AfterTableCreation() {
        int expectedSize = 3;
        List<ExchangeRateResponseDto> allExchangeRate = exchangeRateService.findAll();
        assertEquals(expectedSize, allExchangeRate.size());
    }

    @Order(2)
    @Test
    void findExchangeRateByCodes_Success_IfExchangeRateInTable() {
        ExchangeRateResponseDto responseDto = exchangeRateService.findByCodes(USD_EUR_find_request);
        assertEquals(USD_EUR_expected_response, responseDto);
    }

    @Order(3)
    @Test
    void findExchangeRateByCode_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class, () -> exchangeRateService.findByCodes(USD_RUB_find_request_not_in_table));
    }

    @Order(4)
    @Test
    void findExchangeRateByCode_Throw_IfExchangeRateCodeIncorrect() {
        assertThrows(ValidationException.class, () -> exchangeRateService.findByCodes(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> exchangeRateService.findByCodes(INCORRECT_REQUEST_2));
    }

    @Order(5)
    @Test
    void updateExchangeRate_Success_IfExchangeRateAlreadyInTable() {
        ExchangeRateResponseDto afterUpdate = exchangeRateService.update(USD_EUR_update_request);

        assertEquals(USD_EUR_after_update_response, afterUpdate);
    }

    @Order(6)
    @Test
    void updateExchangeRate_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class,
                () -> exchangeRateService.update(USD_CNY_save_request));
    }

    @Order(7)
    @Test
    void updateExchangeRate_Throw_IfExchangeRateCodeOrRateIsIncorrect() {
        assertThrows(ValidationException.class,
                () -> exchangeRateService.update(INCORRECT_REQUEST_1));

        assertThrows(ValidationException.class,
                () -> exchangeRateService.update(USD_RUB_find_request_not_in_table));
    }

    @Order(8)
    @Test
    void saveExchangeRate_Success_IfExchangeRateNotInTable() {
        int expectedSize = exchangeRateService.findAll().size() + 1;

        ExchangeRateResponseDto responseDto = exchangeRateService.save(USD_CNY_save_request);

        List<ExchangeRateResponseDto> allExchangeRates = exchangeRateService.findAll();

        assertEquals(USD_CNY_expected_after_save, responseDto);
        assertEquals(expectedSize, allExchangeRates.size());
    }

    @Order(9)
    @Test
    void saveExchangeRate_Throw_IfExchangeRateCodeIsIncorrect() {
        assertThrows(ValidationException.class, () -> exchangeRateService.save(INCORRECT_REQUEST_1));
        assertThrows(ValidationException.class, () -> exchangeRateService.save(INCORRECT_REQUEST_2));
    }

    @Order(10)
    @Test
    void saveExchangeRate_Throw_IfExchangeRateAlreadyInTable() {
        int expectedSize = exchangeRateService.findAll().size();

        assertThrows(EntityAlreadyExistsException.class, () -> exchangeRateService.save(USD_EUR_save_request));

        List<ExchangeRateResponseDto> allExchangeRates = exchangeRateService.findAll();
        assertEquals(expectedSize, allExchangeRates.size());
    }


}