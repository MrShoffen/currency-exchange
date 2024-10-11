package org.mrshoffen.exchange.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.dto.request.ExchangeRateRequestDto;
import org.mrshoffen.exchange.dto.response.ExchangeRateResponseDto;
import org.mrshoffen.exchange.exception.ValidationException;
import org.mrshoffen.exchange.service.CurrencyService;
import org.mrshoffen.exchange.service.ExchangeRateService;

import java.io.IOException;

@WebServlet(value = "/exchangeRate/*",name = "ExchangeRate")
public class ExchangeRateServlet extends AbstractBaseHttpServlet {

    @Inject
    private ExchangeRateService exchangeRateService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        exchangeRateService = injector.getInstance(ExchangeRateService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codes = req.getPathInfo().replaceFirst("/", "");

        if (codes == null || codes.length() != 6) {
            throw new ValidationException("Incorrect format of codes");
        }
        ExchangeRateRequestDto requestDto = ExchangeRateRequestDto.builder()
                .baseCurrency(codes.substring(0, 3))
                .targetCurrency(codes.substring(3))
                .build();


        ExchangeRateResponseDto responseDto = exchangeRateService.findByCodes(requestDto);
        writeJsonValueToResponse(resp, responseDto);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String codes = req.getPathInfo().replaceFirst("/", "");
        if (codes.length() != 6) {
            throw new ValidationException("Incorrect format of codes");
        }

        String rate = req.getReader().readLine().replace("rate=", "").trim();;

        ExchangeRateRequestDto requestDto = ExchangeRateRequestDto.builder()
                .baseCurrency(codes.substring(0, 3))
                .targetCurrency(codes.substring(3))
                .rate(rate)
                .build();

        ExchangeRateResponseDto responseDto = exchangeRateService.update(requestDto);

        writeJsonValueToResponse(resp,responseDto);
    }

}
