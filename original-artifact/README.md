# Original Artifact — Weight Tracker (CS 360, pre-enhancement)

This is the original Weight Tracker Android application as submitted in
CS 360: Mobile Architecture and Programming, **before** any CS 499 enhancements.
It is published here as the baseline for the before-and-after comparison in the
ePortfolio.

## Known limitations (identified in the Milestone One code review)

- **Plaintext passwords.** `DatabaseHelper` stores and compares passwords with
  no hashing.
- **No real authentication.** `MainActivity` opens the dashboard without
  validating credentials.
- **Destructive database upgrades.** `DatabaseHelper.onUpgrade()` drops and
  recreates all tables, discarding user data on any schema change.
- **No architectural layering.** Activities call the database class directly;
  presentation, business logic, and data access are mixed.
- **No automated tests and no documentation.**

Each of these is addressed in the enhanced versions of the artifact.

## Files

## Files

- [MainActivity.java](MainActivity.java) — login screen
- [DashboardActivity.java](DashboardActivity.java) — weight history dashboard
- [AddWeightActivity.java](AddWeightActivity.java) — add/edit a weight entry
- [GoalWeightActivity.java](GoalWeightActivity.java) — set goal weight and SMS preference
- [DatabaseHelper.java](DatabaseHelper.java) — SQLite persistence (CRUD for users, weights, goals)
- [SessionManager.java](SessionManager.java) — session persistence via SharedPreferences
