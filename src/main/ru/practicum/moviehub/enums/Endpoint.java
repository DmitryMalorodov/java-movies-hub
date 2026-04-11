package ru.practicum.moviehub.enums;

public enum Endpoint {
    MOVIES("/movies");

    private final String endpoint;

    Endpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
