package com.kalinov.carsitty.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class HttpResponseUtil {
    public static void sendJsonResponse(HttpServletResponse response, int status, Object responseBody) throws IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        writer.print(new ObjectMapper().writeValueAsString(responseBody));
        writer.flush();
    }

    public static HashMap<String, String> getErrorContent(String errorMessage) {
        HashMap<String, String> errorContent = new HashMap<>();
        errorContent.put("problem", errorMessage);
        return errorContent;
    }
}