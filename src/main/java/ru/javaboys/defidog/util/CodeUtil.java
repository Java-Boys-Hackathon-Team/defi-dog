package ru.javaboys.defidog.util;

import java.util.Random;

public class CodeUtil {
    private static final Random RANDOM = new Random();

    public static String randomCode() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

}
