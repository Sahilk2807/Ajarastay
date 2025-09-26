package com.ajarastay.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUtil {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(data));
    }
}

