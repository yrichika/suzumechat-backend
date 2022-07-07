package com.example.suzumechat.testutil.random.pattern;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * This class is only for testing. NOT SECURE random string.
 */
public class RandomString {

    public static final String caps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = caps.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";
    public static final String alphanumeric = caps + lower + digits;
    private final Random random;

    public RandomString() {
        random = new Random();
    }

    public String alphanumeric() {
        return alphanumeric(5);
    }

    public String alphanumeric(int length) {
        return randomWith(length, alphanumeric);
    }

    public String alphanumericLower() {
        return alphanumericLower(5);
    }

    public String alphanumericLower(int length) {
        return randomWith(length, lower + digits);
    }

    public String numeric() {
        return numeric(5);
    }

    public String numeric(int length) {
        return randomWith(length, digits);
    }

    private String randomWith(int length, String charSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charSet.length());
            char randomChar = charSet.charAt(index);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

}
