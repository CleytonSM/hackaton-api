package br.com.connectai.api.helpers;

import java.util.Random;

public class GenerateCodeHelper {
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 5;
    private static final Random random = new Random();

    /**
     * Generates a random 5-character alphanumeric code
     * @return A random alphanumeric string of length 5
     */
    public static String generateRandomCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC_CHARS.length());
            code.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
        }

        return code.toString();
    }
}
