package com.kalinov.carsitty.component;

import com.kalinov.carsitty.dto.ExceptionDto;
import com.kalinov.carsitty.util.HttpResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws IOException {
        response.addHeader("WWW-Authenticate", "BasicNoBrowserPrompt realm=\"" + this.getRealmName() + "\"");
        HttpResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                new ExceptionDto(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpResponseUtil.getErrorContent("Wrong username or password")));
    }

    @Override
    public void afterPropertiesSet() {
        this.setRealmName("Carsitty");
        super.afterPropertiesSet();
    }
}