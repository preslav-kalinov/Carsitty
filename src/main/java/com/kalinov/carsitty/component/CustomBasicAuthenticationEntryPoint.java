package com.kalinov.carsitty.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalinov.carsitty.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws IOException {
        response.addHeader("WWW-Authenticate", "BasicNoBrowserPrompt realm=\"" + this.getRealmName() + "\"");
        this.createAndSendUnauthorizedResponse(response);
    }

    @Override
    public void afterPropertiesSet() {
        this.setRealmName("Carsitty");
        super.afterPropertiesSet();
    }

    private HashMap<String, String> getErrorContent(String errorMessage) {
        HashMap<String, String> errorContent = new HashMap<>();
        errorContent.put("problem", errorMessage);
        return errorContent;
    }

    private void sendResponse(HttpServletResponse httpServletResponse, String responseBody) throws IOException {
        PrintWriter writer = httpServletResponse.getWriter();
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        writer.print(responseBody);
        writer.flush();
    }

    private void createAndSendUnauthorizedResponse(HttpServletResponse httpServletResponse) throws IOException {
        if (httpServletResponse.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.FORBIDDEN.getReasonPhrase(), getErrorContent(
                    "Authorization not enough"));
            String responseBody = new ObjectMapper().writeValueAsString(exceptionDto);
            sendResponse(httpServletResponse, responseBody);
            return;
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.UNAUTHORIZED.getReasonPhrase(), getErrorContent(
                "Wrong username or password"));
        String responseBody = new ObjectMapper().writeValueAsString(exceptionDto);
        sendResponse(httpServletResponse, responseBody);
    }
}