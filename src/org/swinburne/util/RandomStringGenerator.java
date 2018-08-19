package org.swinburne.util;

import java.util.Locale;
import java.util.Random;


public class RandomStringGenerator {

    private static Random random = new Random();

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String digits = "0123456789";

    private static final String alphanum = upper + lower + digits;

    public static String generateRandomString(int length) {
        if (length < 1) return null;

        String result = "";

        for (int i = 0; i < length; i++) {
            result += alphanum.charAt(random.nextInt(alphanum.length()));
        }

        return result;
    }
}
