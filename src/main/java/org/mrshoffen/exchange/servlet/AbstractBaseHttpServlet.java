package org.mrshoffen.exchange.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mrshoffen.exchange.utils.DependencyManager;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;

public abstract class AbstractBaseHttpServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected final Injector injector = DependencyManager.getInjector();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getMethod().equals("PATCH")){
            doPatch(req,resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg = "http.method_post_not_supported";
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
    }

    protected final void writeJsonValueToResponse(HttpServletResponse resp, Object value) throws IOException {
        objectMapper.writeValue(resp.getWriter(),value);
    }


}
