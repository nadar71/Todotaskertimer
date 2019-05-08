package com.example.android.todolist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.todolist.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
* -----------------------------------------------------------------------------------------
* TaskAdapter for create binding ViewHolders toRecycleView (Task's description, priority)
* -----------------------------------------------------------------------------------------
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewRowHolder> {

    // Date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Handle item clicks
    final private ItemClickListener mItemClickListener;

    // Holds task data
    private List<TaskEntry> mTaskEntries;
    private Context         mContext;

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    /**
     * ----------------------------------------------------------------------------------
     * TaskAdapter's Constructor :
     * @param context  the current Context
     * @param listener the ItemClickListener
     * ----------------------------------------------------------------------------------
     */
    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * ----------------------------------------------------------------------------------
     * Inflate list's each view/row layout.
     * @return new TaskViewRowHolder, holds task_layout view for each task
     * ----------------------------------------------------------------------------------
     */
    @Override
    public TaskViewRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to each view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewRowHolder(view);
    }

    /**
     * ----------------------------------------------------------------------------------
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     * @param holder   ViewHolder to bind Cursor data to
     * @param position dataposition in Cursor
     * ----------------------------------------------------------------------------------
     */
    @Override
    public void onBindViewHolder(TaskViewRowHolder holder, int position) {
        // Determine the values of the wanted data
        TaskEntry taskEntry = mTaskEntries.get(position);
        String description  = taskEntry.getDescription();
        int priority        = taskEntry.getPriority();
        String updatedAt    = dateFormat.format(taskEntry.getUpdatedAt());

        //Set values
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);

        // Programmatically set the text/color for the priority TextView
        // String priorityString = "" + priority; // to convert int to String
        holder.priorityView.setText(Integer.toString(priority));

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    }


    /**
    * ---------------------------------------------------------------------------------------------
    * Helper method for selecting the correct priority circle color.
    * P1 = red, P2 = orange, P3 = yellow
    * ---------------------------------------------------------------------------------------------
     */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }


    /**
    * ----------------------------------------------------------------------------------
    * Returns the number of items to display.
    * ----------------------------------------------------------------------------------
     */
    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }


    /**
    * ----------------------------------------------------------------------------------
    * At data changes, updates taskEntries list in RecycleView.
    * Notifies adapter to use the new values
    * ----------------------------------------------------------------------------------
     */
    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        //data changed, refresh the view : notify the related observers
        notifyDataSetChanged();
    }

    /**
    * ----------------------------------------------------------------------------------
    * Return an item in list at defined position
    * ----------------------------------------------------------------------------------
     */
    public TaskEntry getTaskAtPosition(int position){
        return mTaskEntries.get(position);
    }

    /**
    * ----------------------------------------------------------------------------------
    * Get all the tasks list
    * ----------------------------------------------------------------------------------
     */
    public List<TaskEntry> getTasks(){
        return mTaskEntries;
    }


    /**
    * ----------------------------------------------------------------------------------
    * Implemented in calling class, e.g. MainActivity
    * ----------------------------------------------------------------------------------
     */
    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }


    /**
    * ----------------------------------------------------------------------------------
    * Inner class for creating ViewHolders
    * ----------------------------------------------------------------------------------
     */
    class TaskViewRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        // TaskViewHolders Constructor
        // @param itemView view inflated in onCreateViewHolder
        public TaskViewRowHolder(View itemView) {
            super(itemView);

            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView       = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView        = itemView.findViewById(R.id.priorityTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }



}