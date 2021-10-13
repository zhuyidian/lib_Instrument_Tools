package com.coocaa.remoteplatform.core.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.LocalSocket;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName: Utils
 * @Author: XuZeXiao
 * @CreateDate: 3/16/21 3:17 PM
 * @Description:
 */
public class Utils {
    public static void startForeground(String channelId, String channelName, int icon, Service service) {
        Notification.Builder builder = new Notification.Builder(service);
        builder.setOngoing(true).setSmallIcon(icon).setAutoCancel(false).setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
        service.startForeground(1, builder.build());
    }

    public static void startService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void injectParams(Context context, Map<String, String> params) {
        Intent intent = new Intent();
        intent.setAction(Constant.PLATFORM_SERVICE_ACTION);
        intent.setPackage(Constant.HOST_PACKAGE_NAME);
        for (Map.Entry<String, String> item : params.entrySet()) {
            intent.putExtra(item.getKey(), item.getValue());
        }
        startService(context, intent);
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static void saveToSp(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String readFromSp(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void startReceive(LocalSocket socket, IIPCSocketReceiver receiver, String tag) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            while (true) {
                String line = "";
                while ((line = reader.readLine()) != null) {
                    receiver.onReceive(line, socket);
                    Log.i(tag, "startReceive: onReceive");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startSend(LocalSocket socket, BlockingQueue<IPCSocketData> queue, String tag) {
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        try {
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            while (true) {
                IPCSocketData data = queue.take();
                printWriter.println(data.toJsonString());
                printWriter.flush();
                Log.i(tag, "startSend: flush()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
