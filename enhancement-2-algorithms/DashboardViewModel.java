package com.example.weighttracker.ui.dashboard;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weighttracker.SessionManager;
import com.example.weighttracker.data.WeightTrackerRepository;
import com.example.weighttracker.data.model.Goal;
import com.example.weighttracker.data.model.WeightEntry;
import com.example.weighttracker.trend.TrendSummary;
import com.example.weighttracker.trend.WeightTrendAnalyzer;

public class DashboardViewModel extends ViewModel {

    private final WeightTrackerRepository repository;
    private final SessionManager sessionManager;
    private final Executor backgroundExecutor;
    private final MutableLiveData<List<WeightEntry>> weights = new MutableLiveData<>(Collections.emptyList());
    private final MutableLiveData<Goal> goal = new MutableLiveData<>();
    private final MutableLiveData<TrendSummary> trendSummary = new MutableLiveData<>();

    public DashboardViewModel(WeightTrackerRepository repository, SessionManager sessionManager) {
        this(repository, sessionManager, Executors.newSingleThreadExecutor());
    }

    public DashboardViewModel(WeightTrackerRepository repository, SessionManager sessionManager,
                               Executor backgroundExecutor) {
        this.repository = repository;
        this.sessionManager = sessionManager;
        this.backgroundExecutor = backgroundExecutor;
    }

    public LiveData<List<WeightEntry>> getWeights() {
        return weights;
    }

    public LiveData<Goal> getGoal() {
        return goal;
    }

    public LiveData<TrendSummary> getTrendSummary() {
        return trendSummary;
    }

    public String getUsername() {
        return sessionManager.getUsername();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void refresh() {
        backgroundExecutor.execute(this::loadAndPublish);
    }

    public void deleteWeight(int entryId) {
        backgroundExecutor.execute(() -> {
            repository.deleteWeight(entryId);
            loadAndPublish();
        });
    }

    public void logout() {
        sessionManager.clearSession();
    }

    @Override
    protected void onCleared() {
        if (backgroundExecutor instanceof ExecutorService) {
            ((ExecutorService) backgroundExecutor).shutdown();
        }
    }

    private void loadAndPublish() {
        int userId = sessionManager.getUserId();
        List<WeightEntry> entries = repository.getWeights(userId);
        Goal userGoal = repository.getGoal(userId);
        Double goalWeight = userGoal == null ? null : userGoal.getGoalWeight();
        TrendSummary summary = WeightTrendAnalyzer.analyze(entries, goalWeight, LocalDate.now());

        weights.postValue(entries);
        goal.postValue(userGoal);
        trendSummary.postValue(summary);
    }
}
