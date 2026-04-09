package ru.practicum.moviehub.http;

import org.junit.jupiter.api.*;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

public class MoviesApiTest {
    static HttpClient client;
    private static MoviesServer server;
    static final String BASE = "http://localhost:8080";
    static final String EXP_CONTENT_TYPE = "application/json; charset=UTF-8";
    static final HttpResponse.BodyHandler<String> responseBodyHandler =
            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);

    static final String CODE_ERROR_MESSAGE = "Ожидаемый код ответа '%d' не соответствует фактическому '%d'";
    static final String CONTENT_TYPE_ERROR_MESSAGE = "Ожидаемый Content-Type ответа '%s' не соответствует фактическому '%s'";

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        server = new MoviesServer(new MoviesStore(), 8080);
        server.start();
    }

    @BeforeEach
    public void beforeEach() {
        MoviesStore.clearStore();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    HttpRequest makeGetRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .GET()
                .build();
    }

    HttpRequest makePostRequest(String endpoint, String jsonBody, String contentType) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    void checkStatusCodeAndContentType(int statusCode, HttpResponse<String> response) {
        // Проверка кода ответа
        isEqualTo(statusCode, response.statusCode(), CODE_ERROR_MESSAGE);

        // Проверка заголовка Content-Type
        String contentTypeHeaderValue = response.headers().firstValue("Content-Type").orElse("");
        isEqualTo(EXP_CONTENT_TYPE, contentTypeHeaderValue, CONTENT_TYPE_ERROR_MESSAGE);
    }
}