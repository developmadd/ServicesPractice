package com.madd.madd.servicespractice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/*
    This class implements a service that simulates a long time task
    and that task is executed in a second thread, when the task is finished
    the system throws an notification to the user, due it is executed in a
    service, the notification is thrown even if te activity is in background
    A handler is used to communicate between threads.
 */

public class ServiceCounter extends Service {

    final String channelId = "MSGService";
    final String channelName = "MSGService";
    final int notificationId = 45;
    private CounterHandler handler;

    @Override
    public void onCreate() {
        super.onCreate();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if( channel == null ) {
                channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Progreso")
                .setContentText("Espere porfavor")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setDefaults(Notification.DEFAULT_ALL);

        // Create a custom handler implementation object,
        // Due this handler is created in main thread, we can update UI
        // or have interaction with user
        handler = new CounterHandler();
        handler.setBehaviour(new CounterHandler.CounterInterface() {
            @Override
            public void taskFinished(int max, int progress) {

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                if( progress != max ) {
                    notification.setProgress(max, progress, false);
                } else {
                    notification.setProgress(0,0, false);
                    notification.setContentTitle("Finalizado")
                            .setContentText("Proceso finalizado");
                }
                Log.i("PROGRESS",progress + "/"+ max);
                notificationManager.notify(notificationId, notification.build());

            }

        });

    }








    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Duration is defined in intent where the service is thrown
        final int duration = intent.getExtras().getInt("duration");

        // Create a new thread where the long task will be executed
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0 ; i < duration ; i++) {
                    task();

                    Bundle bundle = new Bundle();
                    bundle.putInt("MAX", duration);
                    bundle.putInt("PROGRESS", i+1);
                    Message message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
                stopSelf();

            }
        }).start();

        return START_STICKY;
    }


    void task() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }












    // Own handler implementation which handle the messages sent from work thread
    // This class only executes the correct method depending of the message, method are given by
    // a defined interface.
    static class CounterHandler extends Handler{

        CounterInterface counterInterface;

        void setBehaviour(CounterInterface counterInterface) {
            this.counterInterface = counterInterface;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress = msg.getData().getInt("PROGRESS");
            int max = msg.getData().getInt("MAX");
            counterInterface.taskFinished(max,progress);
        }

        interface CounterInterface {
            void taskFinished(int max, int progress);
        }

    }






}