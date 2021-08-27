package com.coocaa.remoteplatform.baseability.abilities.terminal;

import android.net.LocalSocket;
import android.os.Build;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.sdk.RemotePlatform;
import com.google.gson.Gson;
import com.remoteplatform.commom.Constant;

import java.io.Serializable;

/**
 * @ClassName: TerminalImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:33 PM
 * @Description:
 */
public class TerminalImpl extends AbsAbility {
    private static final String TAG = "TerminalImpl";
    static final String COMMAND_DESTINATION = "/client2web_term";
    static final String TOPIC_NAME = "term";
    private Gson gson = new Gson();
    private TerminalSession terminalSession;

    static class TerminalContent implements Serializable {
        public static final String TOPIC_NAME_TERM = "term";
        public String topicName;

        public boolean isTerminal() {
            return TOPIC_NAME_TERM.equalsIgnoreCase(topicName);
        }
    }


    @Override
    public void handleMessage(RemoteCommand command) {
        if (Constant.CMD_TYPE_CLOSE_SOCKET_CHANNEL == command.cmdType) {
            disconnect();
        } else if (Constant.CMD_TYPE_OPEN_SOCKET_CHANNEL == command.cmdType) {
            connect();
        }
        command.replyFinish(mContext).reply();
    }

    @Override
    public String getName() {
        return null;
    }

    private void connect() {
        Log.i(TAG, "connect: ");
        RemotePlatform.getInstance().subScribeTopic(TOPIC_NAME, new IIPCSocketReceiver() {
            @Override
            public void onReceive(String msg, LocalSocket client) {
                IPCSocketData data = gson.fromJson(msg, IPCSocketData.class);
                String payLoad = data.content;
                Log.i(TAG, "onReceive: " + payLoad);
                final RemoteCommand remoteCommand = gson.fromJson(payLoad, RemoteCommand.class);
                if (terminalSession == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    terminalSession = new TerminalSession("/system/bin/sh", "/", null, null, new TerminalSession.SessionChangedCallback() {
                        @Override
                        public void onTextChanged(TerminalSession changedSession, String text) {
                            remoteCommand.status = "700";
                            remoteCommand.content = text;
                            send(remoteCommand);
                        }

                        @Override
                        public void onTitleChanged(TerminalSession changedSession) {

                        }

                        @Override
                        public void onSessionFinished(TerminalSession finishedSession) {

                        }

                        @Override
                        public void onClipboardText(TerminalSession session, String text) {

                        }

                        @Override
                        public void onBell(TerminalSession session) {

                        }

                        @Override
                        public void onColorsChanged(TerminalSession session) {

                        }
                    });
                    terminalSession.initializeEmulator(100, 100);
                }
                Log.d(TAG, "onNext: remoteCommand.content:" + remoteCommand.content);
                terminalSession.write(remoteCommand.content);
            }
        });
    }

    private void send(RemoteCommand command) {
        RemotePlatform.getInstance().sendTopicMessage("data", TOPIC_NAME, gson.toJson(command));
    }

    private void disconnect() {
        Log.i(TAG, "disconnect: ");
        if (terminalSession != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                terminalSession.finishIfRunning();
                terminalSession = null;
            }
        }
        RemotePlatform.getInstance().unSubScribeTopic(TOPIC_NAME);
    }
}
