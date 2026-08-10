package com.example.weighttracker.security;

/** Password complexity rules enforced at account creation. */
public final class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    private PasswordValidator() {
    }

    public static boolean meetsComplexityRules(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            hasLetter = hasLetter || Character.isLetter(c);
            hasDigit = hasDigit || Character.isDigit(c);
        }
        return hasLetter && hasDigit;
    }

    public static String describeRules() {
        return "Password must be at least " + MIN_LENGTH + " characters and include a letter and a number.";
    }
}
