package com.example.deepanshu.todoapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    long todoId;
    Todo caseTodo;
    TodoDao todoDao;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        todoId = intent.getLongExtra(IntentConstants.TODO,-1);

        TodoDatabase database = TodoDatabase.getInstance(context);
        todoDao = database.todoDao();

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.large_icon);

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                caseTodo = todoDao.getTodoById(todoId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentTitle(caseTodo.getTodoName())
                        .setContentText(caseTodo.getTodoDesc())
                        .setDefaults(NotificationCompat.DEFAULT_LIGHTS|NotificationCompat.DEFAULT_SOUND)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setSmallIcon(R.drawable.small_icon)
                        .setLargeIcon(bitmap);

                Intent resultIntent = new Intent(context,MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) todoId,resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.
                        getSystemService(context.NOTIFICATION_SERVICE);

                notificationManager.notify((int) todoId,builder.build());

            }
        }.execute();


    }
}
