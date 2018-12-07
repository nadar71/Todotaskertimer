/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * ---------------------------------------------------------------------------------------------
 * MainActivity
 * Show the task list and keep updated through ViewModel/LiveData
 * ---------------------------------------------------------------------------------------------
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter  mAdapter;

    // Db reference
    private AppDatabase mDb;

    /**
     * ---------------------------------------------------------------------------------------------
     * onCreate
     * @param savedInstanceState
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init db
        mDb = AppDatabase.getsInstance(getApplicationContext());

        // Set the RecyclerView's view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set LinearLayout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        // -----------------------------------------------------------------------------------------
        // Swipe for delete task; using ItemTouchHelper on RecyclerView
        // * ItemTouchHelper : enables touch behaviour on ViewHolder and callback to perform it
        // -----------------------------------------------------------------------------------------
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final  RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // delete item in db
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskEntry taskToDelete = mAdapter.getTaskAtPosition(viewHolder.getAdapterPosition());
                        mDb.taskDao().deleteTask(taskToDelete);
                        // DON'T NEED to update tasks list view because we use LiveData
                    }
                });


            }
        }).attachToRecyclerView(mRecyclerView);

        // ---------------------
        // FAB for new task
        // ---------------------
        FloatingActionButton fabButton = findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });



        // Using ViewModel/LiveData to show/keep update list
        // ---------------------------------------------------
        // active LiveData and register thus activity as observer
        setupViewModel();
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Recycle touch an item callback to update/modify task
     * @param itemId
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent for UPDATE
        Intent updateTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
        updateTaskIntent.putExtra(AddTaskActivity.EXTRA_TASK_ID,itemId);
        startActivity(updateTaskIntent);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private void setupViewModel() {
        // ---------------------------------------------------
        // Using ViewModel/LiveData to show/keep update list
        // ---------------------------------------------------
        // keep list data updated with LiveData and ViewModel which encapsulate it
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // retrieve all the data from viewModel for the RecycleView
        LiveData<List<TaskEntry>> tasks = viewModel.getTasks();

        // set data in the observer and update the RecycleView through adapter
        tasks.observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(TAG, "Received data from db via LiveData");
                // Update tasklist through adapter
                mAdapter.setTasks(taskEntries);
            }
        });
    }



    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent settingsIntent = new Intent(this, MainSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
