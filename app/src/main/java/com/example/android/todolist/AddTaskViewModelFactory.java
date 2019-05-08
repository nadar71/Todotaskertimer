package com.example.android.todolist;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.example.android.todolist.database.AppDatabase;


/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Factory for AddTaskViewModel object : used to inject into viewModel the mTaskId
 * through create method
 * check :
 * https://github.com/googlesamples/android-architecture-components/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/viewmodel/ProductViewModel.java
 * https://github.com/googlesamples/android-architecture-components/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/ui/ProductFragment.java
 * for better explanation
 * -------------------------------------------------------------------------------------------------
 */
public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase db;
    private final int mTaskId;

    public AddTaskViewModelFactory(AppDatabase db, int mTaskId) {
        this.db = db;
        this.mTaskId = mTaskId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTaskViewModel(db,mTaskId);
    }

}
