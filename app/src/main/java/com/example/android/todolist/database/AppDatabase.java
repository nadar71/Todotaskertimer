 package com.example.android.todolist.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

 /**
  * -----------------------------------------------------------------------------
  * AppDatabase db
  * -----------------------------------------------------------------------------
  */
 @Database(entities = {TaskEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static AppDatabase sInstance;


    public static AppDatabase getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG, "Creating a new db instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        //.allowMainThreadQueries()  // TEMPORARY FOR DEBUG !!!
                        .build();
            }
        }



        Log.d(LOG_TAG, "Return the db instance.");
        return sInstance;
    }

    public abstract TaskDao taskDao();


}
