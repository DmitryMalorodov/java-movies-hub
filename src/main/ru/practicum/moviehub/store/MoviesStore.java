package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesStore {
    private static Map<Long, Movie> movies;

    public MoviesStore() {
        movies = new HashMap<>();
    }

    public Map<Long, Movie> getMovies() {
        return movies;
    }

    public static void clearStore() {
        movies.clear();
    }

    public Movie addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return movie;
    }

    public Movie getMovie(Long movieId) {
        return movies.get(movieId);
    }

    public Movie deleteMovie(Long movieId) {
        return movies.remove(movieId);
    }

    public List<Movie> getMoviesByYear(Integer year) {
        return movies.values().stream()
                .filter(movie -> movie.getYear().equals(year))
                .toList();
    }
}