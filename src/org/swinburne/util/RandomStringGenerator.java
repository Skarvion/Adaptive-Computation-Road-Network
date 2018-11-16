package org.swinburne.util;

import java.util.Locale;
import java.util.Random;

/**
 * Generates random string used to create a unique ID.
 */
public class RandomStringGenerator {

    // Boundary of produceable letters of the generator
    private static Random random = new Random();
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lower = upper.toLowerCase(Locale.ROOT);
    private static final String digits = "0123456789";
    private static final String alphanum = upper + lower + digits;

    /**
     * Generate random string based on the given private data boundary.
     * @param length lenght of the random string
     * @return random string with specified length
     */
    public static String generateRandomString(int length) {
        if (length < 1) return null;

        String result = "";

        for (int i = 0; i < length; i++) {
            result += alphanum.charAt(random.nextInt(alphanum.length()));
        }

        return result;
    }
}
