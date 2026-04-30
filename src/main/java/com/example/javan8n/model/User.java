package com.example.javan8n.model;

public class User {
    private final String name;
    private final String email;
    private final String query;

    public User(String name, String email, String query) {
        this.name = normalize(name);
        this.email = normalize(email);
        this.query = normalize(query);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getQuery() {
        return query;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
