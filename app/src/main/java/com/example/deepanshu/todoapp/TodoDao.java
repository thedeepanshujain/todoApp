package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by deepanshu on 14/7/17.
 */

@Dao
public interface TodoDao {

    @Query("SELECT * from todo")
    List<Todo> getAllTodo();

    @Insert
    void insertInDb(Todo todo);

    @Delete
    void deleteFromDb(Todo todo);
}
