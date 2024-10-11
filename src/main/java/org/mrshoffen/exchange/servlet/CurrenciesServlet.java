package org.mrshoffen.exchange.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.dto.request.CurrencyRequestDto;
import org.mrshoffen.exchange.dto.response.CurrencyResponseDto;
import org.mrshoffen.exchange.service.CurrencyService;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;

@WebServlet(value = "/currencies", name = "Currencies")
public class CurrenciesServlet extends AbstractBaseHttpServlet {

    @Inject
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        currencyService = injector.getInstance(CurrencyService.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyRequestDto requestDto = CurrencyRequestDto.builder()
                .name(req.getParameter("name"))
                .code(req.getParameter("code"))
                .sign(req.getParameter("sign"))
                .build();

        CurrencyResponseDto responseDto =  currencyService.saveCurrency(requestDto);

        resp.setStatus(SC_CREATED);
        writeJsonValueToResponse(resp, responseDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> allCurrencies = currencyService.findAll();

        writeJsonValueToResponse(resp, allCurrencies);
    }
}
