package ru.practicum.moviehub.http.movies;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.MoviesApiTest;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static ru.practicum.moviehub.enums.Endpoint.MOVIES;
import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

public class GetMovieByIdTests extends MoviesApiTest {

    @Test
    void getMovieById() throws IOException, InterruptedException {
        //создание тестовых данных
        List<Movie> expMovies = createAndAddMovies();
        Long randomId = expMovies.get(Helper.getRandomInt(expMovies.size())).getId();
        String endpoint = MOVIES.getEndpoint() + "/" + randomId;

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        String body = resp.body().trim();
        Movie actMovie = Helper.jsonToType(body, Movie.class);
        Movie expMovie = MoviesServer.getMovies().get(randomId);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(200, resp);

        checkMovie(expMovie, actMovie);
    }

    @Test
    void getMovieByDoesNotExistsId() throws IOException, InterruptedException {
        List<Movie> expMovies = createAndAddMovies();
        long randomId = expMovies.size() + Helper.getRandomInt(100);
        String endpoint = MOVIES.getEndpoint() + "/" + randomId;

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(404, resp);

        ErrorResponse errorResponse = Helper.jsonToClass(resp.body(), ErrorResponse.class);
        isEqualTo(errorResponse.getError(), "Фильм не найден",
                "Ожидаемый тип ошибки '%s' не соответствует фактическому '%s'");
    }

    @Test
    void getMovieByNotNumberId() throws IOException, InterruptedException {
        createAndAddMovies();
        String endpoint = MOVIES.getEndpoint() + "/" + "g";

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(400, resp);

        ErrorResponse errorResponse = Helper.jsonToClass(resp.body(), ErrorResponse.class);
        isEqualTo(errorResponse.getError(), "Некорректный ID",
                "Ожидаемый тип ошибки '%s' не соответствует фактическому '%s'");
    }
}
