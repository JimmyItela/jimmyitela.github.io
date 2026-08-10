package com.example.weighttracker.data.room;

import com.example.weighttracker.data.UserDao;
import com.example.weighttracker.data.model.User;

/**
 * Implements the domain-facing {@link UserDao} contract over the Room-generated {@link RoomUserDao},
 * translating between {@link UserEntity} and {@link User}. This is the seam the DAO-interface
 * design from Category One was built for: swapping the persistence technology underneath the
 * repository requires only this class, not any change to {@code WeightTrackerRepository} or a ViewModel.
 */
public class RoomUserDaoAdapter implements UserDao {

    private final RoomUserDao roomUserDao;

    public RoomUserDaoAdapter(RoomUserDao roomUserDao) {
        this.roomUserDao = roomUserDao;
    }

    @Override
    public boolean createUser(String username, String salt, String passwordHash) {
        return roomUserDao.insert(new UserEntity(0, username, salt, passwordHash)) != -1;
    }

    @Override
    public boolean usernameExists(String username) {
        return roomUserDao.usernameExists(username);
    }

    @Override
    public User getUserByUsername(String username) {
        UserEntity entity = roomUserDao.findByUsername(username);
        return entity == null ? null : new User(entity.id, entity.username, entity.salt, entity.passwordHash);
    }
}
