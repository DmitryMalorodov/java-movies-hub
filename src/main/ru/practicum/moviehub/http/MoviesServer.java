package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.http.handlers.MoviesHandler;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class MoviesServer {
    private final HttpServer server;
    private static MoviesStore moviesStore;

    public MoviesServer(MoviesStore moviesStore, Integer port) {
        MoviesServer.moviesStore = moviesStore;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/movies", new MoviesHandler());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать HTTP-сервер", e);
        }
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    public static void addMovies(List<Movie> movies) {
        movies.forEach(movie -> moviesStore.addMovie(movie));
    }

    public static Movie addMovie(String title, Integer year) {
        return moviesStore.addMovie(new Movie(title, year));
    }

    public static Movie getMovie(Long movieId) {
        return moviesStore.getMovie(movieId);
    }

    public static Movie deleteMovie(Long movieId) {
        return moviesStore.deleteMovie(movieId);
    }

    public static List<Movie> getMoviesByYear(Integer year) {
        return moviesStore.getMoviesByYear(year);
    }

    public static Map<Long, Movie> getMovies() {
        return moviesStore.getMovies();
    }
}