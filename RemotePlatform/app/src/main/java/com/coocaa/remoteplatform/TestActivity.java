package com.coocaa.remoteplatform;

import android.app.Activity;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.coocaa.remoteplatform.core.service.RemotePlatformService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @ClassName: Main
 * @Author: XuZeXiao
 * @CreateDate: 3/12/21 9:16 PM
 * @Description:
 */
public class TestActivity extends Activity {
    private static final String TAG = "TestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent();
        i.setClassName(getApplication(), RemotePlatformService.class.getName());
        Log.i("service[", TAG+"$onCreate start RemotePlatformService");
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(i);
        } else {
            startService(i);
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LocalServerSocket serverSocket = null;
//                LocalSocket remote = null;
//                BufferedReader bufferedReader = null;
//                try {
//                    serverSocket = new LocalServerSocket("xzx");
//                    while (true) {
//                        remote = serverSocket.accept();
//                        bufferedReader = new BufferedReader(new InputStreamReader(remote.getInputStream()));
//                        String data = null;
//                        while ((data = bufferedReader.readLine()) != null) {
//                            Log.i("xzx", "run:" + data);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        bufferedReader.close();
//                        remote.close();
//                        serverSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    PrintWriter writer = null;
//                    LocalSocket client = new LocalSocket();
//                    client.connect(new LocalSocketAddress("xzx"));
//                    while (true) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        writer = new PrintWriter(client.getOutputStream());
//                        writer.println("xzx");
//                        writer.flush();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
    }
}
