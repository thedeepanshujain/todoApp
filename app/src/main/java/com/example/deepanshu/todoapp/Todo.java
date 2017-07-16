package com.example.deepanshu.todoapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by deepanshu on 14/7/17.
 */

@Entity (tableName = "todo")
public class Todo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mTodoId;

    @ColumnInfo(name = "name")
    private String mTodoName;

    @ColumnInfo(name = "category")
    private Category mTodoCategory;

    @ColumnInfo(name = "date")
    private Date mTodoDate;

    @ColumnInfo(name = "time")
    private long mTodoTime;

    @ColumnInfo(name = "desc")
    private String mTodoDesc;

    @ColumnInfo(name = "priority")
    private int mTodoPriority;

    @ColumnInfo(name = "alarm")
    private boolean mTodoSetAlarm;

// TODO: 16/7/17 done option for todo  
//    @ColumnInfo(name = "done")
//    private boolean mTodoDone;

    public long getTodoId() {
        return mTodoId;
    }

    public void setTodoId(long mTodoId) {
        this.mTodoId = mTodoId;
    }

    public String getTodoName() {
        return mTodoName;
    }

    public void setTodoName(String mTodoName) {
        this.mTodoName = mTodoName;
    }

    public Category getTodoCategory() {
        return mTodoCategory;
    }

    public void setTodoCategory(Category mTodoCategory) {
        this.mTodoCategory = mTodoCategory;
    }

    public Date getTodoDate() {
        return mTodoDate;
    }

    public void setTodoDate(Date mTodoDate) {
        this.mTodoDate = mTodoDate;
    }

    public long getTodoTime() {
        return mTodoTime;
    }

    public void setTodoTime(long mTodoTime) {
        this.mTodoTime = mTodoTime;
    }

    public String getTodoDesc() {
        return mTodoDesc;
    }

    public void setTodoDesc(String mTodoDesc) {
        this.mTodoDesc = mTodoDesc;
    }

    public int getTodoPriority() {
        return mTodoPriority;
    }

    public void setTodoPriority(int mTodoPriority) {
        this.mTodoPriority = mTodoPriority;
    }

    public boolean isTodoSetAlarm() {
        return mTodoSetAlarm;
    }

    public void setTodoSetAlarm(boolean mTodoSetAlarm) {
        this.mTodoSetAlarm = mTodoSetAlarm;
    }

    public String getIcon() {
        return String.valueOf(mTodoName.charAt(0)).toUpperCase();
    }
}
