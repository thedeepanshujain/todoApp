package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by deepanshu on 16/7/17.
 */

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();


    @Insert
    void newCat(Category category);

    @Delete
    void removeCat(Category category);
}
