# Enhancement Three — Databases

Migration of the persistence layer from a hand-written `SQLiteOpenHelper` to the
**Room** persistence library, fixing the most serious defect from the Milestone
One code review: an `onUpgrade` that dropped every table (and all user data) on
any schema change.

## What changed

- **Room entities and DAOs.** `UserEntity`, `WeightEntity`, and `GoalEntity`
  define the schema; the `Room*Dao` classes provide compile-time-verified queries.
- **Non-destructive migration.** `AppDatabase.MIGRATION_2_3` rebuilds each table
  with a create-copy-drop-rename pattern, so every existing row is copied forward
  before anything is dropped — no user data is lost on upgrade.
- **Indexing.** A composite index on `(user_id, entry_date)` optimizes the
  dashboard's dominant query.
- **Adapter pattern.** The `Room*DaoAdapter` classes implement the existing
  `UserDao` / `WeightDao` / `GoalDao` interfaces on top of Room, so the
  repository and every ViewModel work unchanged — the interfaces built in
  Enhancement One made this migration a drop-in.
- **Aggregate query.** `WeeklyAverage` supports a weekly-average SQL aggregation.

## Key files

- [AppDatabase.java](AppDatabase.java) — Room database, singleton, and the non-destructive `MIGRATION_2_3`
- [UserEntity.java](UserEntity.java) · [WeightEntity.java](WeightEntity.java) · [GoalEntity.java](GoalEntity.java) — Room schema
- [RoomUserDao.java](RoomUserDao.java) · [RoomWeightDao.java](RoomWeightDao.java) · [RoomGoalDao.java](RoomGoalDao.java) — Room DAOs
- [RoomUserDaoAdapter.java](RoomUserDaoAdapter.java) · [RoomWeightDaoAdapter.java](RoomWeightDaoAdapter.java) · [RoomGoalDaoAdapter.java](RoomGoalDaoAdapter.java) — adapters bridging Room DAOs to the app's interfaces
- [WeeklyAverage.java](WeeklyAverage.java) — weekly-average aggregate result
