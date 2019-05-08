package com.example.android.todolist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import java.util.List;


/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving all the tasks
 * -------------------------------------------------------------------------------------------------
 */
public class MainViewModel extends AndroidViewModel {

    private final static String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getsInstance(this.getApplication());
        Log.d(TAG, "Retrieving the tasks from db in vViewModel");
        tasks = db.taskDao().loadAllTasks();



    }

    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }




}
