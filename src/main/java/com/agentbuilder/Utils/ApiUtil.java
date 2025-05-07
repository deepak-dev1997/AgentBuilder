package com.agentbuilder.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiUtil {

    public static String callApi(String url,
                                 Map<String, String> params,
                                 Map<String, String> headers,
                                 Map<String, String> requestBody)  // ⬅️ now a Map
            throws IOException, InterruptedException {

        /* 1️⃣ Build full URL */
        String query = (params == null || params.isEmpty()) ? "" :
                params.entrySet().stream()
                        .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                                + "="
                                + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));

        String fullUrl = query.isBlank() ? url
                : url + (url.contains("?") ? "&" : "?") + query;

        /* 2️⃣ Prepare request */
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(30));

        if (headers != null) headers.forEach(builder::header);

        // convert body map → JSON only if present
        if (requestBody != null && !requestBody.isEmpty()) {
            String json = new ObjectMapper().writeValueAsString(requestBody);
            builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
        } else {
            builder.GET();
        }

        /* 3️⃣ Send */
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        /* 4️⃣ Success? return body else null */
        int status = response.statusCode();
        return (status >= 200 && status < 300) ? response.body() : null;
    }
}
