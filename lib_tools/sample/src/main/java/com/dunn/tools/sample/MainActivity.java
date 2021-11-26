package com.dunn.tools.sample;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                double count = 0;
                while(true) {
                    count++;
                    Log.i(TAG, "send ---> thread=" + Thread.currentThread()+", count="+count);
                    reply(Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);  //1776
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                double count = 0;
                while(true) {
                    count++;
                    Log.i(TAG, "send ---> thread=" + Thread.currentThread()+", count="+count);
                    reply(Thread.currentThread().getName());
                    try {
                        Thread.sleep(3000);  //1188
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void reply(String threadName) {
        Intent intent = new Intent();
        intent.putExtra("params", threadName);
        intent.setAction("coocaa.intent.action.remote.platform.reply");
        intent.setPackage("com.dunn.tools.sample");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
