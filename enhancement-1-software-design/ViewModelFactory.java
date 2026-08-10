package com.example.weighttracker.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttracker.SessionManager;
import com.example.weighttracker.data.WeightTrackerRepository;
import com.example.weighttracker.data.room.AppDatabase;
import com.example.weighttracker.data.room.RoomGoalDaoAdapter;
import com.example.weighttracker.data.room.RoomUserDaoAdapter;
import com.example.weighttracker.data.room.RoomWeightDaoAdapter;
import com.example.weighttracker.ui.addweight.AddWeightViewModel;
import com.example.weighttracker.ui.dashboard.DashboardViewModel;
import com.example.weighttracker.ui.goal.GoalWeightViewModel;
import com.example.weighttracker.ui.login.LoginViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WeightTrackerRepository repository;
    private final SessionManager sessionManager;

    public ViewModelFactory(Context context) {
        Context appContext = context.getApplicationContext();
        AppDatabase database = AppDatabase.getInstance(appContext);
        this.repository = new WeightTrackerRepository(
                new RoomUserDaoAdapter(database.userDao()),
                new RoomWeightDaoAdapter(database.weightDao()),
                new RoomGoalDaoAdapter(database.goalDao()));
        this.sessionManager = new SessionManager(appContext);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(repository, sessionManager);
        }
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(repository, sessionManager);
        }
        if (modelClass.isAssignableFrom(AddWeightViewModel.class)) {
            return (T) new AddWeightViewModel(repository, sessionManager);
        }
        if (modelClass.isAssignableFrom(GoalWeightViewModel.class)) {
            return (T) new GoalWeightViewModel(repository, sessionManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
    }
}
