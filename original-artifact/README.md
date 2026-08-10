# Original Artifact — Weight Tracker (CS 360, pre-enhancement)

This is the original version of the Weight Tracker Android application, as
submitted in CS 360: Mobile Architecture and Programming, **before** CS 499
enhancements have been made.

## Known limitations (listed during the code review of Milestone One)

- **Password storage and verification in plaintext form.**
  Passwords are stored in `DatabaseHelper` in plaintext and compared with no
  use of hashing.
- **No password verification or authentication.**
  `MainActivity` opens the dashboard without checking credentials.
- **Destructive upgrade to the database.**
  `DatabaseHelper.onUpgrade()` destroys all tables without copying old
  data.
- **Absence of architectural layering.**
  The application's Activities interact with `DatabaseHelper`, meaning
  that the presentation, business logic, and data access layers are mixed
  together.
- **Absence of test cases and documentation.**

All of these problems are solved in the enhanced artifact versions.


## Files

- [MainActivity.java](MainActivity.java) — login screen
- [DashboardActivity.java](DashboardActivity.java) — weight history dashboard
- [AddWeightActivity.java](AddWeightActivity.java) — add/edit a weight entry
- [GoalWeightActivity.java](GoalWeightActivity.java) — set goal weight and SMS preference
- [DatabaseHelper.java](DatabaseHelper.java) — SQLite persistence (CRUD for users, weights, goals)
- [SessionManager.java](SessionManager.java) — session persistence via SharedPreferences
