package org.mrshoffen.exchange.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.dto.request.ExchangeRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeResponseDto;
import org.mrshoffen.exchange.service.ExchangeRateService;
import org.mrshoffen.exchange.service.ExchangeService;

import java.io.IOException;

@WebServlet(value = "/exchange", name = "Exchange")
public class ExchangeServlet extends AbstractBaseHttpServlet {

    private ExchangeService exchangeService;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        ExchangeRequestDto requestDto = ExchangeRequestDto.builder()
                .baseCurrencyCode(baseCurrencyCode)
                .targetCurrencyCode(targetCurrencyCode)
                .amount(amount)
                .build();

        ExchangeResponseDto responseDto = exchangeService.exchange(requestDto);

        writeJsonValueToResponse(resp, responseDto);
    }

}
