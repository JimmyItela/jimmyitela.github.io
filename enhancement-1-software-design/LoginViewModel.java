package com.example.weighttracker.ui.login;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weighttracker.SessionManager;
import com.example.weighttracker.data.AuthResult;
import com.example.weighttracker.data.WeightTrackerRepository;

public class LoginViewModel extends ViewModel {

    private final WeightTrackerRepository repository;
    private final SessionManager sessionManager;
    private final Executor backgroundExecutor;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();

    public LoginViewModel(WeightTrackerRepository repository, SessionManager sessionManager) {
        this(repository, sessionManager, Executors.newSingleThreadExecutor());
    }

    public LoginViewModel(WeightTrackerRepository repository, SessionManager sessionManager,
                           Executor backgroundExecutor) {
        this.repository = repository;
        this.sessionManager = sessionManager;
        this.backgroundExecutor = backgroundExecutor;
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void login(String username, String password) {
        backgroundExecutor.execute(() -> applyResult(repository.login(username, password)));
    }

    public void createAccount(String username, String password) {
        backgroundExecutor.execute(() -> applyResult(repository.createAccount(username, password)));
    }

    @Override
    protected void onCleared() {
        if (backgroundExecutor instanceof ExecutorService) {
            ((ExecutorService) backgroundExecutor).shutdown();
        }
    }

    private void applyResult(AuthResult result) {
        if (result.isSuccess()) {
            sessionManager.saveUserSession(result.getUser().getId(), result.getUser().getUsername());
        }
        authResult.postValue(result);
    }
}
