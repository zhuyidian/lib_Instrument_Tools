package com.dunn.tools.time.temp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dunn.tools.log.LogUtil;
import com.google.gson.Gson;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        LogUtil.i("time","onReceive: boot=" + intent.getBooleanExtra("boot", false));
        LogUtil.i("time","onReceive: shutdown=" + intent.getBooleanExtra("shutdown", false));
    }
}