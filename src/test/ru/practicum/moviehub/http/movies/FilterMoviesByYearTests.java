package ru.practicum.moviehub.http.movies;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.http.MoviesApiTest;
import ru.practicum.moviehub.http.helpers.ListOfMoviesTypeToken;
import ru.practicum.moviehub.model.Movie;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.moviehub.enums.Endpoint.MOVIES;
import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

public class FilterMoviesByYearTests extends MoviesApiTest {

    @Test
    void getMoviesByYear() throws IOException, InterruptedException {
        List<Movie> expMovies = createAndAddMovies();
        String endpoint = MOVIES.getEndpoint() + "?year=" + 2011;

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(200, resp);

        String body = resp.body().trim();
        List<Movie> movies = Helper.jsonToType(body, new ListOfMoviesTypeToken().getType());

        isEqualTo(3, movies.size(),
                "Ожидаемый размер списка фильмов '%d' не соответствует фактическому '%d'");

        Map<Long, Movie> expMoviesMap = expMovies.stream().collect(Collectors.toMap(Movie::getId, movie -> movie));
        movies.forEach(movie -> {
            Movie expMovie = expMoviesMap.get(movie.getId());
            checkMovie(expMovie, movie);
        });
    }

    @Test
    void getMoviesByYearThatDoesNotExists() throws IOException, InterruptedException {
        createAndAddMovies();
        String endpoint = MOVIES.getEndpoint() + "?year=" + LocalDate.now().getYear() + Helper.getRandomInt(10);

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(200, resp);

        String body = resp.body().trim();
        List<Movie> movies = Helper.jsonToType(body, new ListOfMoviesTypeToken().getType());

        isEqualTo(0, movies.size(),
                "Ожидаемый размер списка фильмов '%d' не соответствует фактическому '%d'");
    }

    @Test
    void getMoviesByYearThatNotNumber() throws IOException, InterruptedException {
        createAndAddMovies();
        String endpoint = MOVIES.getEndpoint() + "?year=" + "L";

        HttpResponse<String> resp = client.send(makeGetRequest(endpoint), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(400, resp);
    }
}
