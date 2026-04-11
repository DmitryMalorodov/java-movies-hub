package ru.practicum.moviehub.http;

import org.junit.jupiter.api.*;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

public class MoviesApiTest {
    protected static HttpClient client;
    private static MoviesServer server;
    static final String BASE = "http://localhost:8080";
    protected static final String EXP_CONTENT_TYPE = "application/json; charset=UTF-8";
    protected static final HttpResponse.BodyHandler<String> responseBodyHandler =
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

    protected HttpRequest makeGetRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .GET()
                .build();
    }

    protected HttpRequest makePostRequest(String endpoint, String jsonBody, String contentType) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
    }

    protected HttpRequest makeDeleteRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .DELETE()
                .build();
    }

    protected void checkStatusCodeAndContentType(int statusCode, HttpResponse<String> response) {
        // Проверка кода ответа
        isEqualTo(statusCode, response.statusCode(), CODE_ERROR_MESSAGE);

        // Проверка заголовка Content-Type
        String contentTypeHeaderValue = response.headers().firstValue("Content-Type").orElse("");
        isEqualTo(EXP_CONTENT_TYPE, contentTypeHeaderValue, CONTENT_TYPE_ERROR_MESSAGE);
    }

    protected List<Movie> createAndAddMovies() {
        List<Movie> expMovies = List.of(
                new Movie("Механик", 2010),
                new Movie("Профессионал", 2011),
                new Movie("Защитник", 2011),
                new Movie("Несносные боссы", 2011)
        );
        MoviesServer.addMovies(expMovies);
        return expMovies;
    }

    protected void checkMovie(Movie expMovie, Movie actMovie) {
        isEqualTo(expMovie.getId(), actMovie.getId(),
                "Ожидаемый id добавленного фильма '%d' не соответствует фактическому '%d'");
        isEqualTo(expMovie.getTitle(), actMovie.getTitle(),
                "Ожидаемое название добавленного фильма '%s' не соответствует фактическому '%s'");
        isEqualTo(expMovie.getYear(), actMovie.getYear(),
                "Ожидаемый год выпуска добавленного фильма '%d' не соответствует фактическому '%d'");
    }
}