package ru.practicum.moviehub.http.movies;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.http.MoviesApiTest;
import ru.practicum.moviehub.model.Movie;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static ru.practicum.moviehub.enums.Endpoint.MOVIES;

public class DeleteMovieTests extends MoviesApiTest {

    @Test
    void deleteMovie() throws IOException, InterruptedException {
        List<Movie> expMovies = createAndAddMovies();
        long randomId = expMovies.get(Helper.getRandomInt(expMovies.size())).getId();
        String endpoint = MOVIES.getEndpoint() + "/" + randomId;

        HttpResponse<String> resp = client.send(makeDeleteRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(204, resp);
    }

    @Test
    void deleteMovieByDoesNotExistsId() throws IOException, InterruptedException {
        List<Movie> expMovies = createAndAddMovies();
        long randomId = expMovies.size() + Helper.getRandomInt(100);
        String endpoint = MOVIES.getEndpoint() + "/" + randomId;

        HttpResponse<String> resp = client.send(makeDeleteRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(404, resp);
    }

    @Test
    void deleteMovieByNotNumberId() throws IOException, InterruptedException {
        createAndAddMovies();
        String endpoint = MOVIES.getEndpoint() + "/" + ".";

        HttpResponse<String> resp = client.send(makeDeleteRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(400, resp);
    }
}
