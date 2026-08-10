package com.example.weighttracker.data;

import com.example.weighttracker.data.model.User;

/**
 * Outcome of a login or account-creation attempt. Failed logins always carry
 * the same generic message so the UI cannot be used to enumerate valid
 * usernames.
 */
public class AuthResult {

    private final boolean success;
    private final User user;
    private final String errorMessage;

    private AuthResult(boolean success, User user, String errorMessage) {
        this.success = success;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public static AuthResult success(User user) {
        return new AuthResult(true, user, null);
    }

    public static AuthResult error(String errorMessage) {
        return new AuthResult(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
