package com.example.deepanshu.todoapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by deepanshu on 14/7/17.
 */

@Entity (tableName = "todo")
public class Todo {

    @PrimaryKey(autoGenerate = true)
    private long mTodoId;
    private String mTodoName;
    private String mTodoCategory;
    private Date mTodoDate;
    private long mTodoTime;
    private String mTodoDesc;
    private int mTodoPriority;
    private boolean mTodoSetAlarm;

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

    public String getTodoCategory() {
        return mTodoCategory;
    }

    public void setTodoCategory(String mTodoCategory) {
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
