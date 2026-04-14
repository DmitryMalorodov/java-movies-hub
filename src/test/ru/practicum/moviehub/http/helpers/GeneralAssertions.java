package ru.practicum.moviehub.http.helpers;

import org.junit.jupiter.api.Assertions;

public class GeneralAssertions {

    public static <T> void isEqualTo(T expValue, T actValue, String errorMessage) {
        Assertions.assertEquals(expValue, actValue, String.format(errorMessage, expValue, actValue));
    }
}
