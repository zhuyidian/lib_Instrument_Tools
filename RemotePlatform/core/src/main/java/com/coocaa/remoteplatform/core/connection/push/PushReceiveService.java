package com.coocaa.remoteplatform.core.connection.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.coocaa.remoteplatform.core.R;
import com.coocaa.remoteplatform.core.common.Utils;
import com.coocaa.remoteplatform.core.service.Main;

import java.util.HashMap;

import static com.coocaa.remoteplatform.core.connection.push.PushConstant.ACTION_PUSH;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.CURRENT_TIME;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.MAP_EKY;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.MSG_ID_KEY;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.PUSH_APP_PKG;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.REGID_RESULT_KEY;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.THIRD_PKGNAME;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.THRID_APP_HANDLE;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.THRID_APP_HANDLE_RESULT;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.THRID_APP_MSG_TYPE;
import static com.coocaa.remoteplatform.core.connection.push.PushConstant.THRID_APP_RECEIVE;

/**
 * @ClassName: CoocaaPushService
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:27 PM
 * @Description:
 */
public class PushReceiveService extends IntentService {
    private static final String TAG = "PushReceive";

    public PushReceiveService() {
        super("PushService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.startForeground("PushReceiveServiceId", "PushReceiveServiceName", R.drawable.ic_launcher, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PushCommand pushCommand = new PushCommand(intent);
        Log.i(TAG, "onReceive: " + pushCommand);
        Main.getInstance(getApplicationContext()).getPushChannel().onReceiveMessage(pushCommand);
        replyReceiveResult(pushCommand);
        commandExecuteResult(pushCommand);
    }

    private void replyReceiveResult(PushCommand pushCommand) {
        Intent intent = new Intent(ACTION_PUSH);
        intent.setPackage(PUSH_APP_PKG);
        intent.putExtra(THRID_APP_MSG_TYPE, THRID_APP_RECEIVE);

        HashMap<String, String> map = new HashMap<>();
        map.put(MSG_ID_KEY, pushCommand.msgId);
        map.put(REGID_RESULT_KEY, pushCommand.pushId);
        map.put(THIRD_PKGNAME, getPackageName());
        map.put(CURRENT_TIME, String.valueOf(System.currentTimeMillis()));

        Bundle bundle = new Bundle();
        bundle.putSerializable(MAP_EKY, map);
        intent.putExtras(bundle);
        startService(intent);
    }

    public boolean commandExecuteResult(PushCommand pushCommand) {
        Intent intent = new Intent(ACTION_PUSH);
        intent.setPackage(PUSH_APP_PKG);
        intent.putExtra(THRID_APP_MSG_TYPE, THRID_APP_HANDLE);

        HashMap<String, String> map = new HashMap<>();
        map.put(MSG_ID_KEY, pushCommand.msgId);
        map.put(REGID_RESULT_KEY, pushCommand.pushId);
        map.put(THIRD_PKGNAME, getPackageName());
        map.put(CURRENT_TIME, String.valueOf(System.currentTimeMillis()));
        map.put(THRID_APP_HANDLE_RESULT, "消息处理成功");

        Bundle bundle = new Bundle();
        bundle.putSerializable(MAP_EKY, map);
        intent.putExtras(bundle);
        startService(intent);
        return false;
    }
}
