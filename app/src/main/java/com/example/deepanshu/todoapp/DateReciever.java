package com.example.deepanshu.todoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
            // TODO: 18/7/17 handle date change and mainActivity running 
        }
    }
}
