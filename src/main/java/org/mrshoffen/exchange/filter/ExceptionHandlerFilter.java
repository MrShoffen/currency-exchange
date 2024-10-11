package org.mrshoffen.exchange.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.dto.response.ErrorResponseDto;
import org.mrshoffen.exchange.exception.EntityAlreadyExistsException;
import org.mrshoffen.exchange.exception.EntityNotFoundException;
import org.mrshoffen.exchange.exception.ValidationException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlerFilter extends HttpFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {
        try {
            chain.doFilter(req, res);
        } catch (Exception exception) {
            int responseCode =
                    switch (exception) {
                        case EntityNotFoundException _ -> SC_NOT_FOUND;
                        case EntityAlreadyExistsException _ -> SC_CONFLICT;
                        case ValidationException _ -> SC_BAD_REQUEST;
                        default -> SC_INTERNAL_SERVER_ERROR;
                    };

            res.setStatus(responseCode);
            objectMapper.writeValue(res.getWriter(), ErrorResponseDto.of(exception.getMessage()));
        }
    }
}
