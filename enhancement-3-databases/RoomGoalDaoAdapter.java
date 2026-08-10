package com.example.weighttracker.data.room;

import com.example.weighttracker.data.GoalDao;
import com.example.weighttracker.data.model.Goal;

/**
 * Implements the domain-facing {@link GoalDao} contract over {@link RoomGoalDao}, preserving the
 * original select-then-insert-or-update semantics of the hand-written {@code saveGoal} rather
 * than restructuring the goals table around a user_id primary key.
 */
public class RoomGoalDaoAdapter implements GoalDao {

    private final RoomGoalDao roomGoalDao;

    public RoomGoalDaoAdapter(RoomGoalDao roomGoalDao) {
        this.roomGoalDao = roomGoalDao;
    }

    @Override
    public long saveGoal(int userId, double goalWeight, String phoneNumber, boolean smsEnabled) {
        GoalEntity existing = roomGoalDao.findByUserId(userId);
        if (existing != null) {
            return roomGoalDao.updateByUserId(userId, goalWeight, phoneNumber, smsEnabled);
        }
        return roomGoalDao.insert(new GoalEntity(0, userId, goalWeight, phoneNumber, smsEnabled));
    }

    @Override
    public Goal getGoalForUser(int userId) {
        GoalEntity entity = roomGoalDao.findByUserId(userId);
        return entity == null ? null : new Goal(entity.goalWeight, entity.phoneNumber, entity.smsEnabled);
    }
}
