# Enhancement Three — Databases

Migrating persistence layer from manually implemented `SQLiteOpenHelper` to using
the **Room** library to fix one major issue from milestone one code review:
an `onUpgrade` that drops every table (and all user data) on any schema change.

## What has been done

- **Entities and DAOs for Room.** `UserEntity`, `WeightEntity`, and `GoalEntity`
  describe the database schema while `Room*Dao` are used to perform queries at
  compile time.
- **Destructive migration.** `AppDatabase.MIGRATION_2_3` recreates every table
  using create+copy+drop+rename strategy so that all the rows are copied before
  tables are being dropped — no user data will be lost during an upgrade.
- **Indexing.** Composite index on `(user_id, entry_date)` helps to optimize
  the query which is executed most often from the dashboard's point of view.
- **Adapter Pattern.** Implementation of existing `UserDao`, `WeightDao` and
  `GoalDao` interfaces by `Room*DaoAdapter` allows using Room seamlessly inside
  the repository as well as in ViewModels — interfaces developed during
  enhancement one allowed easy migration.
- **Aggregate Query.** `WeeklyAverage` provides a way to execute SQL aggregation.

## Key files

- [AppDatabase.java](AppDatabase.java) — Room database, singleton, and the non-destructive `MIGRATION_2_3`
- [UserEntity.java](UserEntity.java) · [WeightEntity.java](WeightEntity.java) · [GoalEntity.java](GoalEntity.java) — Room schema
- [RoomUserDao.java](RoomUserDao.java) · [RoomWeightDao.java](RoomWeightDao.java) · [RoomGoalDao.java](RoomGoalDao.java) — Room DAOs
- [RoomUserDaoAdapter.java](RoomUserDaoAdapter.java) · [RoomWeightDaoAdapter.java](RoomWeightDaoAdapter.java) · [RoomGoalDaoAdapter.java](RoomGoalDaoAdapter.java) — adapters bridging Room DAOs to the app's interfaces
- [WeeklyAverage.java](WeeklyAverage.java) — weekly-average aggregate result
