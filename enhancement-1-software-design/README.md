# Enhancement One — Software Design & Engineering

The Weight Tracker application refactored from a prototype where the UI and data
layer were disconnected into a layered **Model-View-ViewModel (MVVM)**
architecture, with the authentication vulnerabilities from the Milestone One code
review remediated.

## What changed

- **MVVM architecture.** Activities became thin renderers; UI state and logic
  moved into ViewModels wired through a `ViewModelFactory`.
- **Repository + DAO interfaces.** `WeightTrackerRepository` is the single path
  from UI to persistence, depending on the `UserDao` / `WeightDao` / `GoalDao`
  interfaces rather than a concrete database class.
- **Secure credential handling.** `PasswordHasher` replaces plaintext storage
  with salted PBKDF2 (100,000 iterations, constant-time comparison);
  `PasswordValidator` enforces complexity rules; failed logins return one generic
  message so accounts cannot be enumerated.
- **Background-thread authentication.** Login runs on an injectable executor and
  publishes results via `LiveData.postValue`, keeping the deliberately slow hash
  off the UI thread.
- **RecyclerView** replaces the hand-built dashboard table.

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
