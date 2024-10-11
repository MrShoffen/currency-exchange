package org.mrshoffen.exchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@WebFilter(servletNames =
        {"Currencies", "Currency", "ExchangeRates", "ExchangeRate", "Exchange"}
)
public class ContentTypeFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setContentType("application/json");
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());

        chain.doFilter(req, res);
    }
}
