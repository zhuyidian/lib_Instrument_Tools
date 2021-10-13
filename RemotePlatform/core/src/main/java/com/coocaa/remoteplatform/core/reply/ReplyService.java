package com.coocaa.remoteplatform.core.reply;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.coocaa.remoteplatform.core.R;
import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.Utils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.service.Main;

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

    public ReplyService() {
        super("RemoteReplyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.startForeground(CHANNEL_ID, CHANNEL_NAME, R.drawable.ic_launcher, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RemoteCommand command = intent.getParcelableExtra(Constant.INTENT_COMMAND_KEY);
        Log.i(TAG, "onHandleIntent: " + command.toString());
        Main.getInstance(getApplicationContext()).getMessageReply().replyMessage(command);
    }
}
