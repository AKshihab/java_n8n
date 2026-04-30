package com.example.javan8n.client;

import com.example.javan8n.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

public class WebhookClient {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final HttpClient httpClient;

    public WebhookClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String sendUserData(String webhookUrl, User user) throws IOException, InterruptedException {
        validate(webhookUrl, user);

        String payload = toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Webhook request failed: HTTP " + statusCode + " - " + response.body());
        }

        return response.body() == null ? "" : response.body().trim();
    }

    private void validate(String webhookUrl, User user) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalArgumentException("Webhook URL is required.");
        }
        if (user == null) {
            throw new IllegalArgumentException("User data is required.");
        }
        if (user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        if (user.getQuery().isBlank()) {
            throw new IllegalArgumentException("Query is required.");
        }
    }

    private String toJson(User user) {
        return "{" +
                "\"name\":\"" + escapeJson(user.getName()) + "\"," +
                "\"email\":\"" + escapeJson(user.getEmail()) + "\"," +
                "\"query\":\"" + escapeJson(user.getQuery()) + "\"" +
                "}";
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
