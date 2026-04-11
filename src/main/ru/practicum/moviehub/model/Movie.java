package ru.practicum.moviehub.model;

import java.util.concurrent.atomic.AtomicLong;

public class Movie {
    private long id;
    private String title;
    private Integer year;
    private static final AtomicLong count = new AtomicLong(0);

    public Movie(String title, Integer year) {
        this.id = count.incrementAndGet();
        this.title = title;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}