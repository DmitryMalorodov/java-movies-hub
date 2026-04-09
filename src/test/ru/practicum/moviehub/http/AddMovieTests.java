package ru.practicum.moviehub.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.model.Movie;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

public class AddMovieTests extends MoviesApiTest {

    private static List<Movie> getMoviesWithCorrectData() {
        return List.of(
                new Movie("Старый фильм", 1888),
                new Movie("Новый фильм", LocalDate.now().getYear()),
                new Movie("Ф", 2025),
                new Movie("НАЗВАНИЕ 100 СИМВОЛОВ 234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789", 1900)
        );
    }

    @ParameterizedTest
    @MethodSource("getMoviesWithCorrectData")
    void addMovie(Movie movie) throws Exception {
        // Отправка запроса
        HttpResponse<String> resp = sendRequest(movie, EXP_CONTENT_TYPE);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(201, resp);

        // проверка полей фильма
        Movie addedMovie = Helper.jsonToClass(resp.body(), Movie.class);
        isEqualTo(movie.getTitle(), addedMovie.getTitle(),
                "Ожидаемое название добавленного фильма '%s' не соответствует фактическому '%s'");
        isEqualTo(movie.getYear(), addedMovie.getYear(),
                "Ожидаемый год выпуска добавленного фильма '%d' не соответствует фактическому '%d'");
    }

    @Test
    void addMovieCheckHeader() throws Exception {
        Movie movie = new Movie("Форсаж", 2001);

        // Отправка запроса
        HttpResponse<String> resp = sendRequest(movie, "json");

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(415, resp);
    }

    private static List<Movie> getMoviesWithIncorrectYear() {
        return List.of(
                new Movie("Старый фильм", 1887),
                new Movie("Слишком новый фильм", LocalDate.now().getYear() + 1)
        );
    }

    @ParameterizedTest
    @MethodSource("getMoviesWithIncorrectYear")
    void addMovieWithIncorrectYear(Movie movieToAdd) throws Exception {
        // Отправка запроса
        HttpResponse<String> resp = sendRequest(movieToAdd, EXP_CONTENT_TYPE);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(422, resp);

        ErrorResponse errorResponse = Helper.jsonToClass(resp.body(), ErrorResponse.class);
        isEqualTo(errorResponse.getError(), "Ошибка валидации",
                "Ожидаемый тип ошибки '%s' не соответствует фактическому '%s'");
        isEqualTo(errorResponse.getDetails().getFirst(), "год должен быть не меньше 1888 и не больше " + LocalDate.now().getYear(),
                "Ожидаемое описание проблемы '%s' не соответствует фактическому '%s'");
    }

    @Test
    void addMovieWithLongTitle() throws Exception {
        Movie movie = new Movie("НАЗВАНИЕ 101 СИМВОЛОВ 234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567894", 2000);

        // Отправка запроса
        HttpResponse<String> resp = sendRequest(movie, EXP_CONTENT_TYPE);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(422, resp);

        ErrorResponse errorResponse = Helper.jsonToClass(resp.body(), ErrorResponse.class);
        isEqualTo(errorResponse.getError(), "Ошибка валидации",
                "Ожидаемый тип ошибки '%s' не соответствует фактическому '%s'");
        isEqualTo(errorResponse.getDetails().getFirst(), "название не должно содержать больше 100 символов",
                "Ожидаемое описание проблемы '%s' не соответствует фактическому '%s'");
    }

    @Test
    void addMovieWithoutTitle() throws Exception {
        Movie movie = new Movie("", 2000);

        // Отправка запроса
        HttpResponse<String> resp = sendRequest(movie, EXP_CONTENT_TYPE);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(422, resp);

        ErrorResponse errorResponse = Helper.jsonToClass(resp.body(), ErrorResponse.class);
        isEqualTo(errorResponse.getError(), "Ошибка валидации",
                "Ожидаемый тип ошибки '%s' не соответствует фактическому '%s'");
        isEqualTo(errorResponse.getDetails().getFirst(), "название не должно быть пустым",
                "Ожидаемое описание проблемы '%s' не соответствует фактическому '%s'");
    }

    private HttpResponse<String> sendRequest(Movie movie, String contentType) throws Exception {
        return client.send(
                makePostRequest(
                        "/movies",
                        Helper.toJson(movie),
                        contentType),
                responseBodyHandler);
    }
}
