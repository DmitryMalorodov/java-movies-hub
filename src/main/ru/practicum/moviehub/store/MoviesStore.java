package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.HashMap;
import java.util.Map;

public class MoviesStore {
    private static Map<Long, Movie> movies;

    public MoviesStore() {
        movies = new HashMap<>();
    }

    public static Map<Long, Movie> getMovies() {
        return movies;
    }

    public static void clearStore() {
        movies.clear();
    }

    public Movie addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return movie;
    }
}