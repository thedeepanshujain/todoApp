package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by deepanshu on 16/7/17.
 */

@Entity (tableName = "category")
public class Category {

    @PrimaryKey
    private String mCategory;

    public Category(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
