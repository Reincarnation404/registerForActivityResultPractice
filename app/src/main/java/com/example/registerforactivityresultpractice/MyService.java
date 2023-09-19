package com.example.registerforactivityresultpractice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("MyService downloading...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("MyService download completed!");
            }
        },3000);
        System.out.println("MyService download almost done...");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("MyService的onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}