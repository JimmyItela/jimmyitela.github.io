package com.example.weighttracker.data;

import com.example.weighttracker.data.model.User;

public interface UserDao {

    boolean createUser(String username, String salt, String passwordHash);

    boolean usernameExists(String username);

    
    User getUserByUsername(String username);
}
