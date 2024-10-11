package org.mrshoffen.exchange.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet(value = "/exchangeRates", name = "ExchangeRates")
public class ExchangeRatesServlet extends AbstractBaseHttpServlet {
    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto>  allExchangeRates = exchangeRateService.findAll();

        writeJsonValueToResponse(resp,allExchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto requestDto = ExchangeRateRequestDto.builder()
                .baseCurrency(req.getParameter("baseCurrencyCode"))
                .targetCurrency(req.getParameter("targetCurrencyCode"))
                .rate(req.getParameter("rate"))
                .build();

        ExchangeRateResponseDto responseDto =  exchangeRateService.save(requestDto);

        resp.setStatus(SC_CREATED);
        writeJsonValueToResponse(resp, responseDto);
    }

}
