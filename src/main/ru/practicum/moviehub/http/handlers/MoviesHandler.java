package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.BaseHttpHandler;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static ru.practicum.moviehub.Helper.jsonToClass;
import static ru.practicum.moviehub.Helper.toJson;

public class MoviesHandler extends BaseHttpHandler {
    private static final int MIN_YEAR = 1888;
    private static final int MAX_TITLE_LENGTH = 100;

    @Override
    public void handle(HttpExchange ex) throws IOException {
        switch (ex.getRequestMethod()) {
            case "GET" -> sendJson(ex, 200, toJson(MoviesStore.getMovies().values()));

            case "POST" -> {
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
                }
            }
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
