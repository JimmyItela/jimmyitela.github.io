# Enhancement One — Software Design & Engineering

The Weight Tracker application updated from the original, where there was no connection between the user interface and the data layer, to a layered MVVM architecture with the security flaws fixed in Milestone One reviewed code.

## What changed

- **MVVM architecture.** Activities became renderers, with UI state and logic now
  held in ViewModels using `ViewModelFactory`.
- **Repository + DAO interfaces.** The `WeightTrackerRepository` is the only way
  UI gets at persistent data, and does so through interfaces (`UserDao`,
  `WeightDao`, and `GoalDao`) instead of a concrete database class.
- **Credential storage security.** Replaced the password storage with hashed
  passwords (PBKDF2, 100,000 iterations, constant-time comparison) using the
  `PasswordHasher` utility; passwords are validated using `PasswordValidator`;
  all login failure messages become generic to prevent enumeration.
- **Executor-based authentication.** Login operation runs on an injectable executor,
  and sends its result using `LiveData.postValue`; this keeps the hashing process
  that is deliberately slow off the UI thread.
- **RecyclerView** used instead of manually created dashboard table.

## Key files

- [LoginViewModel.java](LoginViewModel.java) — login/account-creation logic, background threading
- [WeightTrackerRepository.java](WeightTrackerRepository.java) — single data-access path, business rules
- [PasswordHasher.java](PasswordHasher.java) — salted PBKDF2 hashing with constant-time verification
- [PasswordValidator.java](PasswordValidator.java) — password complexity enforcement
- [AuthResult.java](AuthResult.java) — typed success/error result for auth
- [UserDao.java](UserDao.java) · [WeightDao.java](WeightDao.java) · [GoalDao.java](GoalDao.java) — persistence interfaces
- [ViewModelFactory.java](ViewModelFactory.java) — constructs ViewModels with dependencies
- [WeightEntryAdapter.java](WeightEntryAdapter.java) — RecyclerView adapter for the dashboard
- [MainActivity.java](MainActivity.java) — login screen, now a thin renderer
