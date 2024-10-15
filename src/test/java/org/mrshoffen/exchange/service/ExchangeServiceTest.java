package org.mrshoffen.exchange.service;

import org.junit.jupiter.api.*;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeResponseDto;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExchangeServiceTest {
    private static ExchangeService exchangeService;


    private static ExchangeRequestDto USD_EUR_direct_exchange_request;
    private static ExchangeResponseDto USD_EUR_expected_response;

    private static ExchangeRequestDto EUR_USD_reverse_exchange_request;
    private static ExchangeResponseDto EUR_USD_expected_response;


    private static ExchangeRequestDto EUR_GBP_cross_exchange_request;
    private static ExchangeResponseDto EUR_GBP_expected_response;


    private static ExchangeRequestDto USD_RUB_not_in_table;
    private static ExchangeRequestDto INCORRECT_REQUEST_1;
    private static ExchangeRequestDto INCORRECT_REQUEST_2;

    @BeforeAll
    static void setUpTablesAndDependencies() {
        exchangeService = DependencyManager.getInjector().getInstance(ExchangeService.class);
        DatabaseManipulation.createAndFillTestTables();

        CurrencyResponseDto USD_dto = new CurrencyResponseDto(1, "US Dollar", "USD", "$");
        CurrencyResponseDto EUR_dto = new CurrencyResponseDto(2, "Euro", "EUR", "€");
        CurrencyResponseDto GBP_dto = new CurrencyResponseDto(3, "Pound Sterling", "GBP", "£");


        USD_EUR_direct_exchange_request = ExchangeRequestDto.builder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("EUR")
                .amount("100")
                .build();
        USD_EUR_expected_response = new ExchangeResponseDto(USD_dto, EUR_dto, BigDecimal.valueOf(0.9), BigDecimal.valueOf(100), BigDecimal.valueOf(90.00).setScale(2));


        EUR_USD_reverse_exchange_request = ExchangeRequestDto.builder()
                .baseCurrencyCode("EUR")
                .targetCurrencyCode("USD")
                .amount("100")
                .build();

        EUR_USD_expected_response = new ExchangeResponseDto(EUR_dto, USD_dto, BigDecimal.valueOf(1.111111), BigDecimal.valueOf(100), BigDecimal.valueOf(111.11));


        EUR_GBP_cross_exchange_request = ExchangeRequestDto.builder()
                .baseCurrencyCode("EUR")
                .targetCurrencyCode("GBP")
                .amount("100")
                .build();
        EUR_GBP_expected_response = new ExchangeResponseDto(EUR_dto, GBP_dto, BigDecimal.valueOf(0.833333), BigDecimal.valueOf(100), BigDecimal.valueOf(83.33));

        USD_RUB_not_in_table = ExchangeRequestDto.builder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("RUB")
                .amount("100")
                .build();

        INCORRECT_REQUEST_1 = ExchangeRequestDto.builder()
                .baseCurrencyCode("Ufe")
                .targetCurrencyCode("RUsdfB3")
                .amount("100")
                .build();

        INCORRECT_REQUEST_2 = ExchangeRequestDto.builder()
                .baseCurrencyCode("USD")
                .targetCurrencyCode("EUR")
                .amount(null)
                .build();


    }


    @AfterAll
    static void dropTables() {
        DatabaseManipulation.dropTestTables();
    }

    @Order(1)
    @Test
    void exchangeDirectExchange_Success() {
        ExchangeResponseDto responseExchange = exchangeService.exchange(USD_EUR_direct_exchange_request);
        assertEquals(USD_EUR_expected_response, responseExchange);
    }

    @Order(2)
    @Test
    void exchangeReverseExchange_Success() {
        ExchangeResponseDto responseExchange = exchangeService.exchange(EUR_USD_reverse_exchange_request);

        assertEquals(EUR_USD_expected_response, responseExchange);
    }

    @Order(3)
    @Test
    void exchangeCrossExchange_Success() {
        ExchangeResponseDto responseExchange = exchangeService.exchange(EUR_GBP_cross_exchange_request);

        assertEquals(EUR_GBP_expected_response, responseExchange);
    }

    @Order(4)
    @Test
    void exchange_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class,
                () -> exchangeService.exchange(USD_RUB_not_in_table));
    }

    @Order(5)
    @Test
    void exchange_Throw_IfExchangeRateCodeOrRateIsIncorrect() {
        assertThrows(ValidationException.class,
                () -> exchangeService.exchange(INCORRECT_REQUEST_1));

        assertThrows(ValidationException.class,
                () -> exchangeService.exchange(INCORRECT_REQUEST_2));
    }

}