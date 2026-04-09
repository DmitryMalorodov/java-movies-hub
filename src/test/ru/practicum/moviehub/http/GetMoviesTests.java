package ru.practicum.moviehub.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.model.Movie;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.moviehub.http.helpers.GeneralAssertions.isEqualTo;

@DisplayName("Проверка ручки получения фильмов - GET /movies")
public class GetMoviesTests extends MoviesApiTest {

    @Test
    void getMovies_whenEmpty_returnsEmptyArray() throws Exception {
        // Отправка запроса
        HttpResponse<String> resp = client.send(makeGetRequest("/movies"), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(200, resp);

        // проверка размера списка
        checkListSize(resp, new ArrayList<>());
    }

    @Test
    void getMovies_returnsArrayOfMovies() throws Exception {
        //создание тестовых данных
        List<Movie> expMovies = createAndAddMovies();

        // Отправка запроса
        HttpResponse<String> resp = client.send(makeGetRequest("/movies"), responseBodyHandler);

        // Проверка кода ответа и заголовка Content-Type
        checkStatusCodeAndContentType(200, resp);

        // проверка размера списка
        checkListSize(resp, expMovies);
    }

    private void checkListSize(HttpResponse<String> response, List<Movie> expMovies) {
        String body = response.body().trim();
        List<Movie> movies = Helper.jsonToType(body, new ListOfMoviesTypeToken().getType());
        isEqualTo(expMovies.size(), movies.size(),
                "Ожидаемое кол-во фильмов '%d' не соответствует фактическому '%d'");
    }

    private List<Movie> createAndAddMovies() {
        List<Movie> expMovies = List.of(
                new Movie("Механик", 2010),
                new Movie("Профессионал", 2011)
        );
        MoviesServer.addMovies(expMovies);
        return expMovies;
    }
}
