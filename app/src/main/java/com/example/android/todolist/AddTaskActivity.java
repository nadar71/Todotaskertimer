
package com.example.android.todolist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import java.util.Date;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

/**
 * -----------------------------------------------------------------------------
 * Insert/update task activity
 * -----------------------------------------------------------------------------
 */
public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";

    // Extra for the task ID to be received after device rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;

    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();

    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;

    // set the task id as default: will be changed in case of update
    private int mTaskId = DEFAULT_TASK_ID;

    // db reference
    private AppDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        // init db reference
        mDb = AppDatabase.getsInstance(getApplicationContext());

        // restore the task id after rotation, in case savedInstanceState has been created
        // otherwise it remains DEFAULT_TASK_ID set above
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        // check if in update mode : EXTRA_TASK_ID key will be present in intent
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            // change button text for update
            mButton.setText(R.string.update_button);
            // if id is the default one insert the new to be updated
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                // show current data of the task to be updated using LiveData/ViewModel

                // instantiate AddTaskViewModelFactory object to inject mTaskId to ViewModel
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb,mTaskId);

                final AddTaskViewModel viewModel = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);

                // populate the UI in case of upgrade with the data of the mTaksId task
                final LiveData<TaskEntry> task = viewModel.getTask();

                // keep UI updated through observer
                task.observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(@Nullable TaskEntry taskEntry) {
                        // don't need the observe while populating UI
                        task.removeObserver(this);
                        Log.d(TAG, "Received data from db via LiveData");
                        populateUI(taskEntry);
                    }
                });
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * save insert data in case of rotations
     * @param outState
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
    * -----------------------------------------------------------------------------
    * initViews is called from onCreate to init the member variable views
    * -----------------------------------------------------------------------------
    */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
    * -----------------------------------------------------------------------------
    * populateUI would be called to populate the UI when in update mode
    * @param task the taskEntry to populate the UI
    * -----------------------------------------------------------------------------
    */
    private void populateUI(TaskEntry task) {
        if (task == null){
            return;
        }

        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }



    /**
    * -------------------------------------------------------------------------------------
    * onSaveButtonClicked is called when the "save" button is clicked.
    * It retrieves user input and inserts that new task data into the underlying database.
    * -------------------------------------------------------------------------------------
    */
    public void onSaveButtonClicked() {
        // get task attributes from view
        String description = mEditText.getText().toString();
        int priority       = getPriorityFromViews();
        Date date          = new Date();

        // create a new task obj and init with data inserted by user
        final TaskEntry taskEntry = new TaskEntry(description, priority, date);

        // ----------------------------------------
        // Update db using executor
        // ----------------------------------------
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
               if(mTaskId == DEFAULT_TASK_ID) {     // save a new task
                   mDb.taskDao().insertTask(taskEntry);
                   finish();
               }else{                               // update a previous task
                   // set id to the task to update
                   taskEntry.setId(mTaskId);
                   // update task on db
                   mDb.taskDao().updateTask(taskEntry);
               }
            }
        });



    }

    /**
     * -----------------------------------------------------------------------------
     * getPriority is called whenever the selected priority needs to be retrieved
     * -----------------------------------------------------------------------------
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * -----------------------------------------------------------------------------
     * setPriority is called when we receive a task from MainActivity
     * @param priority the priority value
     * -----------------------------------------------------------------------------
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }
}
