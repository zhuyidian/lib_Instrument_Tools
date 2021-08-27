package com.coocaa.remoteplatform.core.connection.socket;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.core.common.Utils;
import com.google.gson.Gson;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: LocalSocketService
 * @Author: XuZeXiao
 * @CreateDate: 4/14/21 8:49 PM
 * @Description:
 */
public class IPCSocketServer {
    private static final String TAG = "IPCSocketServer";
    private LocalServerSocket mSocketService = null;
    private SocketChannel mSocketChannel = null;
    private final Gson mGson = new Gson();
    private final BlockingQueue<IPCSocketData> messageQueue = new LinkedBlockingQueue<>(5);

    public IPCSocketServer(SocketChannel socketChannel) {
        mSocketChannel = socketChannel;
    }

    public void startListen() {
        Log.i("socket[", TAG+"$startListen");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketService = new LocalServerSocket(Constant.HOST_PACKAGE_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        LocalSocket client = mSocketService.accept();
                        handleClient(client);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    private void handleClient(final LocalSocket client) {
        Log.i(TAG, "handle new Client: " + client);
        Log.i("socket[", TAG+"$handleClient: new Client: " + client);
        if (client == null) {
            return;
        }
        startReceiveThread(client, socketReceiver);
        startSendThread(client);
    }

    private void startReceiveThread(final LocalSocket client, IIPCSocketReceiver callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "server start receive socket");
                Utils.startReceive(client, callback, TAG);
                Log.w(TAG, "server end receive socket!!");
            }
        }, "IPCServerReceive-Thread").start();
    }

    private void startSendThread(final LocalSocket client) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "server start send");
                Utils.startSend(client, messageQueue, TAG);
                Log.w(TAG, "server end send!!");
            }
        }, "IPCServerSend-Thread").start();
    }

    private final IIPCSocketReceiver socketReceiver = new IIPCSocketReceiver() {
        @Override
        public void onReceive(String msg, LocalSocket client) {
            IPCSocketData data = mGson.fromJson(msg, IPCSocketData.class);
            if (data == null) {
                return;
            }
            Log.i(TAG, "onReceive: <---" + data.content);
            if ("data".equalsIgnoreCase(data.method)) {
                RemoteCommand command = mGson.fromJson(data.content, RemoteCommand.class);
                mSocketChannel.replyMessage(data.topic, command);
            }
        }
    };

    public synchronized void enqueueMessage(String topic, IPCSocketData message) {
        if (messageQueue.offer(message)) {
            Log.i(TAG, "enqueueMessage to client:---> " + topic);
        } else {
            Log.w(TAG, "enqueueMessage to client failed message queue size:---> " + messageQueue.size());
        }
    }

    public void stop() {
        if (mSocketService != null) {
            try {
                mSocketService.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
