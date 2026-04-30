package com.example.javan8n.controller;

import com.example.javan8n.client.WebhookClient;
import com.example.javan8n.model.User;

import java.io.IOException;

public class AppController {
    private final WebhookClient webhookClient;

    public AppController(WebhookClient webhookClient) {
        this.webhookClient = webhookClient;
    }

    public String submit(String webhookUrl, String name, String email, String query) throws IOException, InterruptedException {
        User user = new User(name, email, query);
        return webhookClient.sendUserData(webhookUrl, user);
    }
}
