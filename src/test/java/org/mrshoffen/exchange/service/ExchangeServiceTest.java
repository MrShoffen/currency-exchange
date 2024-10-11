package org.mrshoffen.exchange.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeResponseDto;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeServiceTest {
    ExchangeService exchangeService = ExchangeService.getInstance();

    private static final CurrencyResponseDto USD = CurrencyResponseDto.builder()
            .id(1).code("USD").name("US Dollar").sign("$").build();
    private static final CurrencyResponseDto EUR = CurrencyResponseDto.builder()
            .id(2).code("EUR").name("Euro").sign("€").build();
    private static final CurrencyResponseDto GBP = CurrencyResponseDto.builder()
            .id(3).code("GBP").name("Pound Sterling").sign("£").build();

    private static final ExchangeRequestDto USD_EUR_direct_exchange_request = ExchangeRequestDto.builder()
            .baseCurrencyCode("USD")
            .targetCurrencyCode("EUR")
            .amount("100")
            .build();
    private static final ExchangeResponseDto USD_EUR_expected_response = ExchangeResponseDto.builder()
            .baseCurrency(USD)
            .targetCurrency(EUR)
            .amount(BigDecimal.valueOf(100))
            .rate(BigDecimal.valueOf(0.9))
            .convertedAmount(BigDecimal.valueOf(90.00).setScale(2))
            .build();

    private static final ExchangeRequestDto EUR_USD_reverse_exchange_request = ExchangeRequestDto.builder()
            .baseCurrencyCode("EUR")
            .targetCurrencyCode("USD")
            .amount("100")
            .build();;
    private static final ExchangeResponseDto EUR_USD_expected_response = ExchangeResponseDto.builder()
            .baseCurrency(EUR)
            .targetCurrency(USD)
            .amount(BigDecimal.valueOf(100))
            .rate(BigDecimal.valueOf(1.111111))
            .convertedAmount(BigDecimal.valueOf(111.11))
            .build();

    private static ExchangeRequestDto EUR_GBP_cross_exchange_request = ExchangeRequestDto.builder()
            .baseCurrencyCode("EUR")
            .targetCurrencyCode("GBP")
            .amount("100")
            .build();
    private static final ExchangeResponseDto EUR_GBP_expected_response = ExchangeResponseDto.builder()
            .baseCurrency(EUR)
            .targetCurrency(GBP)
            .amount(BigDecimal.valueOf(100))
            .rate(BigDecimal.valueOf(0.833333))
            .convertedAmount(BigDecimal.valueOf(83.33))
            .build();

    private static final ExchangeRequestDto USD_RUB_not_in_table = ExchangeRequestDto.builder()
            .baseCurrencyCode("USD")
            .targetCurrencyCode("RUB")
            .amount("100")
            .build();
    private static final ExchangeRequestDto INCORRECT_REQUEST_1 = ExchangeRequestDto.builder()
            .baseCurrencyCode("Ufe")
            .targetCurrencyCode("RUsdfB3")
            .amount("100")
            .build();
    private static final ExchangeRequestDto INCORRECT_REQUEST_2 = ExchangeRequestDto.builder()
            .baseCurrencyCode("USD")
            .targetCurrencyCode("EUR")
            .amount(null)
            .build();

    @BeforeEach
    void initTestData() {
        DatabaseManipulation.createAndFillTestTables();
    }

    @AfterEach
    void dropTestTables() {
        DatabaseManipulation.dropTestTables();
    }

    @Test
    void exchangeDirectExchange_Success(){
        ExchangeResponseDto responseExchange = exchangeService.exchange(USD_EUR_direct_exchange_request);
        assertEquals(USD_EUR_expected_response,responseExchange);
    }

    @Test
    void exchangeReverseExchange_Success(){
        ExchangeResponseDto responseExchange = exchangeService.exchange(EUR_USD_reverse_exchange_request);

        assertEquals(EUR_USD_expected_response,responseExchange);
    }

    @Test
    void exchangeCrossExchange_Success(){
        ExchangeResponseDto responseExchange = exchangeService.exchange(EUR_GBP_cross_exchange_request);

        assertEquals(EUR_GBP_expected_response,responseExchange);
    }

    @Test
    void exchange_Throw_IfExchangeRateNotInTable() {
        assertThrows(EntityNotFoundException.class,
                () -> exchangeService.exchange(USD_RUB_not_in_table));
    }

    @Test
    void exchange_Throw_IfExchangeRateCodeOrRateIsIncorrect() {
        assertThrows(ValidationException.class,
                () -> exchangeService.exchange(INCORRECT_REQUEST_1));

        assertThrows(ValidationException.class,
                () -> exchangeService.exchange(INCORRECT_REQUEST_2));
    }

}