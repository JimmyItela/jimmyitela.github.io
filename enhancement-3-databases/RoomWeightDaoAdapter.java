package com.example.weighttracker.data.room;

import java.util.ArrayList;
import java.util.List;

import com.example.weighttracker.data.WeightDao;
import com.example.weighttracker.data.model.WeightEntry;

/**
* Adapts the domain {@link WeightDao} interface to {@link RoomWeightDao},
* translating between domain models and Room entities.
*/
public class RoomWeightDaoAdapter implements WeightDao {

    private final RoomWeightDao roomWeightDao;

    public RoomWeightDaoAdapter(RoomWeightDao roomWeightDao) {
        this.roomWeightDao = roomWeightDao;
    }

    @Override
    public long insertWeight(int userId, double weight, String entryDate) {
        return roomWeightDao.insert(new WeightEntity(0, userId, weight, entryDate));
    }

    @Override
    public int updateWeight(int entryId, double weight, String entryDate) {
        return roomWeightDao.update(entryId, weight, entryDate);
    }

    @Override
    public int deleteWeight(int entryId) {
        return roomWeightDao.delete(entryId);
    }

    @Override
    public List<WeightEntry> getWeightsForUser(int userId) {
        List<WeightEntry> entries = new ArrayList<>();
        for (WeightEntity entity : roomWeightDao.findByUser(userId)) {
            entries.add(new WeightEntry(entity.id, entity.entryDate, entity.weight));
        }
        return entries;
    }

    @Override
    public WeightEntry getWeightById(int entryId) {
        WeightEntity entity = roomWeightDao.findById(entryId);
        return entity == null ? null : new WeightEntry(entity.id, entity.entryDate, entity.weight);
    }
}
