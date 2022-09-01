package com.dunn.instrument.tools.sample;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * @ClassName: ReplyService
 * @Author: XuZeXiao
 * @CreateDate: 3/9/21 5:16 PM
 * @Description:
 */
public class ReplyService extends IntentService {
    private static final String TAG = "ReplyService";
    private static final String CHANNEL_ID = "ReplyServiceId";
    private static final String CHANNEL_NAME = "ReplyServiceName";
    private static double count = 0;

    public ReplyService() {
        super("RemoteReplyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(CHANNEL_ID, CHANNEL_NAME, R.mipmap.ic_launcher, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        count++;
        String params = intent.getStringExtra("params");
        Log.i(MainActivity.TAG, "Service onHandleIntent: <--- params=" + params+", count="+count);
    }

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
}
