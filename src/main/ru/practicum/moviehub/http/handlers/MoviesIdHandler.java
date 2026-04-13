package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.Helper;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.BaseHttpHandler;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;

import java.io.IOException;

import static ru.practicum.moviehub.Helper.toJson;
import static ru.practicum.moviehub.enums.Endpoint.MOVIES;

public class MoviesIdHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getRawPath();

        switch (ex.getRequestMethod()) {
            case "GET" -> useGetMethod(ex, path);
            case "DELETE" -> useDeleteMethod(ex, path);
            default -> sendNoContent(ex, 405);
        }
    }

    private void useGetMethod(HttpExchange ex, String path) throws IOException {
        String movieId = path.substring((MOVIES.getEndpoint() + "/").length());
        if (!Helper.isNumber(movieId)) {
            sendJson(ex, 400, toJson(new ErrorResponse("Некорректный ID")));
            return;
        }

        Movie movie = MoviesServer.getMovie(Long.valueOf(movieId));
        if (movie != null) {
            sendJson(ex, 200, toJson(movie));
        } else {
            sendJson(ex, 404, toJson(new ErrorResponse("Фильм не найден")));
        }
    }

    private void useDeleteMethod(HttpExchange ex, String path) throws IOException {
        String movieId = path.substring((MOVIES.getEndpoint() + "/").length());
        if (!Helper.isNumber(movieId)) {
            sendNoContent(ex, 400);
            return;
        }

        if (MoviesServer.deleteMovie(Long.valueOf(movieId)) != null) {
            sendNoContent(ex, 204);
        } else {
            sendNoContent(ex, 404);
        }
    }
}
