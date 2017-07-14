package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by deepanshu on 14/7/17.
 */

@Database(entities = {Todo.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TodoDatabase extends RoomDatabase {

    public static final String DB_NAME = "todo_db";
    private static TodoDatabase instance;
    private static Object LOCK = new Object();

    public static TodoDatabase getInstance(Context context){
        if (instance == null){
            synchronized (LOCK){
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),TodoDatabase.class,DB_NAME)
                            .build();
                }
            }
        }
        return instance;
    }

    abstract TodoDao todoDao();
}
