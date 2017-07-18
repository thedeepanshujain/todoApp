package com.example.deepanshu.todoapp;

<<<<<<< HEAD
import android.app.Notification;
=======
>>>>>>> broadcasts
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
<<<<<<< HEAD
import android.graphics.drawable.BitmapDrawable;
=======
>>>>>>> broadcasts
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

<<<<<<< HEAD
=======
    long todoId;
>>>>>>> broadcasts
    Todo caseTodo;
    TodoDao todoDao;

    @Override
<<<<<<< HEAD
    public void onReceive(final Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        TodoDatabase database = TodoDatabase.getInstance(context);
        todoDao = database.todoDao();

        final Bitmap large_icon = BitmapFactory.decodeResource(context.getResources(),R.drawable.large_icon_notify);

        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                caseTodo = todoDao.getTodoById(intent.getLongExtra(IntentConstants.TODO,-1));
=======
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
>>>>>>> broadcasts
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
<<<<<<< HEAD

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setSmallIcon(R.drawable.alarm_small_icon)
                        .setLargeIcon(large_icon)
                        .setContentTitle(caseTodo.getTodoName())
                        .setContentText(caseTodo.getTodoDesc())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);

                Intent resultIntent = new Intent(context,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,1,resultIntent,0);
=======
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
>>>>>>> broadcasts

                builder.setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.
                        getSystemService(context.NOTIFICATION_SERVICE);
<<<<<<< HEAD
                notificationManager.notify((int) caseTodo.getTodoId(),builder.build());
=======

                notificationManager.notify((int) todoId,builder.build());

>>>>>>> broadcasts
            }
        }.execute();


    }
}
