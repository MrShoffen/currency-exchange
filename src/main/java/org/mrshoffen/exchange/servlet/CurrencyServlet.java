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

@WebServlet(value = "/currency/*", name = "Currency")
public class CurrencyServlet extends AbstractBaseHttpServlet {

    @Inject
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        currencyService = injector.getInstance(CurrencyService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().replaceFirst("/", "");
        CurrencyRequestDto requestDto = CurrencyRequestDto.builder()
                .code(code)
                .build();
        CurrencyResponseDto responseDto =  currencyService.findByCode(requestDto);

        writeJsonValueToResponse(resp,responseDto);
    }
}
