package com.coocaa.remoteplatform.baseability.abilities.terminal;

import android.net.LocalSocket;
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
    private RemoteCommand remoteCommand;
    private TerminalSession terminalSession;
    private ClientConnect clientConnect;
    private boolean isTelnetEnable = true;

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
                remoteCommand = gson.fromJson(payLoad, RemoteCommand.class);
                if (isTelnetEnable) {
                    if (clientConnect == null) {
                        clientConnect = new ClientConnect(new ClientConnect.SessionChangedCallback() {
                            @Override
                            public void onSessionStart() {
                            }

                            @Override
                            public void onSessionFinished(String error) {
                                //无法连接telnetd
                                if (error.contains("SocketDisconnect-1")) {
                                    isTelnetEnable = false;
                                    initTerminalSession();
                                } else {
                                    clientConnect = null; //尝试重连
                                }
                            }

                            @Override
                            public void onTextChanged(String text) {
                                remoteCommand.status = "700";
                                remoteCommand.content = text;
                                send(remoteCommand);
                            }
                        });
                        if (clientConnect.connect()) {
                            clientConnect.send("start_telnetd");
                        } else {
                            isTelnetEnable = false;
                            initTerminalSession();
                        }
                    }
                } else {
                    initTerminalSession();
                }
                Log.d(TAG, "onNext: remoteCommand.content:" + remoteCommand.content);
                if (remoteCommand.content == null) {
                    return;
                }
                if (isTelnetEnable) {
                    clientConnect.write(remoteCommand.content);
                } else {
                    terminalSession.write(remoteCommand.content);
                }
            }
        }, Integer.MAX_VALUE);
    }

    private void initTerminalSession() {
        if (terminalSession == null) {
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
                    terminalSession = null;
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
            terminalSession.initializeEmulator(130, 100);
        }
    }

    private void send(RemoteCommand command) {
        RemotePlatform.getInstance().sendTopicMessage("data", TOPIC_NAME, gson.toJson(command));
    }

    private void disconnect() {
        Log.i(TAG, "disconnect: ");
        if (terminalSession != null) {
            terminalSession.finishIfRunning();
            terminalSession = null;
        }
        if (clientConnect != null) {
            clientConnect.finishIfRunning();
            clientConnect = null;
        }
        RemotePlatform.getInstance().unSubScribeTopic(TOPIC_NAME);
    }
}
