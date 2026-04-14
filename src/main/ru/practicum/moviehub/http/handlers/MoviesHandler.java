package ru.practicum.moviehub.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.BaseHttpHandler;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static ru.practicum.moviehub.Helper.jsonToClass;
import static ru.practicum.moviehub.Helper.toJson;
import static ru.practicum.moviehub.enums.Endpoint.MOVIES;

public class MoviesHandler extends BaseHttpHandler {
    private static final int MIN_YEAR = 1888;
    private static final int MAX_TITLE_LENGTH = 100;

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String pathWithQuery = ex.getRequestURI().getRawQuery() == null
                ? ex.getRequestURI().getRawPath()
                : ex.getRequestURI().getRawPath() + "?" + ex.getRequestURI().getRawQuery();

        switch (ex.getRequestMethod()) {
            case "GET" -> useGetMethod(ex, pathWithQuery);
            case "POST" -> usePostMethod(ex);
            default -> sendNoContent(ex, 405);
        }
    }

    private void useGetMethod(HttpExchange ex, String path) throws IOException {
        if (path.equals(MOVIES.getEndpoint())) {
            sendJson(ex, 200, toJson(MoviesServer.getMovies().values()));
        } else if (path.contains(MOVIES.getEndpoint() + "?year=")) {
            String yearQueryValue = path.substring((MOVIES.getEndpoint() + "?year=").length());
            if (!Helper.isNumber(yearQueryValue)) {
                sendJson(ex, 400, toJson(new ErrorResponse("Некорректный параметр запроса — 'year'")));
                return;
            }

            List<Movie> moviesByYear = MoviesServer.getMoviesByYear(Integer.valueOf(yearQueryValue));
            sendJson(ex, 200, toJson(moviesByYear));
        }
    }

    private void usePostMethod(HttpExchange ex) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8)) {
            if (!ex.getRequestHeaders().getFirst("Content-Type").equals(CT_JSON)) {
                sendNoContent(ex, 415);
                return;
            }

            Movie movieToAdd = jsonToClass(isr, Movie.class);

            ErrorResponse errorResponse = validateReqBody(movieToAdd);
            if (!errorResponse.getDetails().isEmpty()) {
                sendJson(ex, 422, toJson(errorResponse));
                return;
            }

            Movie addedMovie = MoviesServer.addMovie(movieToAdd.getTitle(), movieToAdd.getYear());
            sendJson(ex, 201, toJson(addedMovie));
        } catch (JsonSyntaxException e) {
            sendJson(ex, 400, toJson(new ErrorResponse("Невалидный json в теле запроса")));
        }
    }

    private ErrorResponse validateReqBody(Movie reqBody) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации");
        int currentMaxYear = LocalDate.now().getYear();

        if (reqBody.getTitle() == null || reqBody.getTitle().isBlank()) {
            errorResponse.addDetail("название не должно быть пустым");
        } else if (reqBody.getTitle().length() > MAX_TITLE_LENGTH) {
            errorResponse.addDetail("название не должно содержать больше 100 символов");
        }

        if (reqBody.getYear() < MIN_YEAR || reqBody.getYear() > currentMaxYear) {
            errorResponse.addDetail("год должен быть не меньше 1888 и не больше " + currentMaxYear);
        }

        return errorResponse;
    }
}
