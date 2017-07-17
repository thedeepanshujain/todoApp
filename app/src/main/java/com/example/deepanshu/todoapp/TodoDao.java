package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by deepanshu on 14/7/17.
 */

@Dao
public interface TodoDao {

    @Query("SELECT * from todo ORDER BY priority DESC")
    List<Todo> getAllTodo();

    @Query("SELECT * FROM todo WHERE ID = :id")
    Todo getTodoById(long id);

    @Insert
    void insertInDb(Todo todo);

    @Update
    void updateDb(Todo todo);

    @Delete
    void deleteFromDb(Todo todo);
}
