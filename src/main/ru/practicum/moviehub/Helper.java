package ru.practicum.moviehub;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Random;

public class Helper {
    private static final Gson gson = new Gson();
    private static final Random random = new Random();

    private Helper(){
    }

    public static <T> T jsonToClass(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    public static <T> T jsonToClass(InputStreamReader isr, Class<T> tClass) {
        return gson.fromJson(isr, tClass);
    }

    public static <T> T jsonToType(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> String toJson(Collection<T> list) {
        return gson.toJson(list);
    }

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public static boolean isNumber(String string) {
        return string.matches("\\d+");
    }

    public static int getRandomInt(int bound) {
        return random.nextInt(bound);
    }
}
