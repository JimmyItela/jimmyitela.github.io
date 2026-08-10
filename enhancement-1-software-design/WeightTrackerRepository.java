package com.example.weighttracker.data;

import java.util.List;

import com.example.weighttracker.data.model.Goal;
import com.example.weighttracker.data.model.User;
import com.example.weighttracker.data.model.WeightEntry;
import com.example.weighttracker.security.PasswordHasher;
import com.example.weighttracker.security.PasswordValidator;

/**
 * Single path from the ViewModel layer to persistence. Activities and ViewModels never talk to
 * a DAO directly; all business rules (password hashing, complexity checks, generic auth errors)
 * live here so they can be unit tested without any Android framework dependency.
 */
public class WeightTrackerRepository {

    private static final String GENERIC_LOGIN_ERROR = "Invalid username or password.";

    private final UserDao userDao;
    private final WeightDao weightDao;
    private final GoalDao goalDao;

    public WeightTrackerRepository(UserDao userDao, WeightDao weightDao, GoalDao goalDao) {
        this.userDao = userDao;
        this.weightDao = weightDao;
        this.goalDao = goalDao;
    }

    /**
     * Validates credentials against the stored salted hash. Every failure path - unknown
     * username or wrong password - returns the same generic message so a caller cannot use
     * the response to enumerate valid usernames.
     */
    public AuthResult login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return AuthResult.error(GENERIC_LOGIN_ERROR);
        }
        User user = userDao.getUserByUsername(username);
        if (user == null || !PasswordHasher.verify(password, user.getSalt(), user.getPasswordHash())) {
            return AuthResult.error(GENERIC_LOGIN_ERROR);
        }
        return AuthResult.success(user);
    }

    /** Enforces password complexity and username uniqueness, then stores a salted hash. */
    public AuthResult createAccount(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return AuthResult.error("Enter a username and password to create an account.");
        }
        if (!PasswordValidator.meetsComplexityRules(password)) {
            return AuthResult.error(PasswordValidator.describeRules());
        }
        if (userDao.usernameExists(username)) {
            return AuthResult.error("That username already exists. Please log in.");
        }

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(password, salt);
        if (!userDao.createUser(username, salt, hash)) {
            return AuthResult.error("Unable to create account.");
        }
        return AuthResult.success(userDao.getUserByUsername(username));
    }

    public List<WeightEntry> getWeights(int userId) {
        return weightDao.getWeightsForUser(userId);
    }

    public WeightEntry getWeightById(int entryId) {
        return weightDao.getWeightById(entryId);
    }

    /** Inserts a new entry when {@code entryId} is {@code null}, otherwise updates it in place. */
    public boolean saveWeight(int userId, Integer entryId, double weight, String entryDate) {
        if (entryId == null) {
            return weightDao.insertWeight(userId, weight, entryDate) != -1;
        }
        return weightDao.updateWeight(entryId, weight, entryDate) > 0;
    }

    public boolean deleteWeight(int entryId) {
        return weightDao.deleteWeight(entryId) > 0;
    }

    public Goal getGoal(int userId) {
        return goalDao.getGoalForUser(userId);
    }

    public boolean saveGoal(int userId, double goalWeight, String phoneNumber, boolean smsEnabled) {
        return goalDao.saveGoal(userId, goalWeight, phoneNumber, smsEnabled) != -1;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
