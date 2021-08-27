package com.coocaa.remoteplatform.core.dispatcher;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coocaa.remoteplatform.core.clientmanager.Client;
import com.coocaa.remoteplatform.core.clientmanager.IClientManager;
import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.Utils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;

/**
 * @ClassName: MessageDispatcher
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 8:07 PM
 * @Description:
 */
public class MessageDispatcher implements IMessageDispatcher {
    private static final String TAG = "MessageDispatcher";
    private IClientManager mClientManager = null;
    private Context mContext = null;

    public MessageDispatcher(IClientManager mClientManager, Context mContext) {
        this.mClientManager = mClientManager;
        this.mContext = mContext;
    }

    @Override
    public boolean dispatchMessage(RemoteCommand command) {
        String targetId = command.clientId;
        Client client = mClientManager.getClient(targetId);
        if (client == null) {
            Log.w(TAG, "dispatchMessage: can not find target: " + targetId);
            return false;
        }
        return sendCommandToClient(command, client);
    }

    private boolean sendCommandToClient(RemoteCommand command, Client client) {
        Log.i(TAG, "sendCommandToClient: " + client.toString() + " content: " + command.content);
        Log.i(TAG, "sendCommandToClient: RemoteCommand=" + command);
        Intent intent = new Intent();
        intent.setAction(Constant.CLIENT_SERVICE_ACTION);
        intent.setComponent(client.componentName);
        intent.putExtra(Constant.INTENT_COMMAND_KEY, command);
        Utils.startService(mContext, intent);
        return true;
    }
}
