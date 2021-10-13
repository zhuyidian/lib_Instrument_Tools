package com.coocaa.remoteplatform.sdk;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.common.Utils;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: IPCSocketClient
 * @Author: XuZeXiao
 * @CreateDate: 4/14/21 4:48 PM
 * @Description:
 */
public class IPCSocketClient {
    private static final String TAG = "IPCSocketClient";
    private final BlockingQueue<IPCSocketData> messageQueue;
    private LocalSocket server = null;
    private boolean isDisconnectInitiative = false;
    private Thread sendThread = null;
    private Thread receiveThread = null;
    private String topic = null;
    private IIPCSocketReceiver callback = null;

    public IPCSocketClient(int messageQueueLength) {
        messageQueue = new LinkedBlockingQueue<>(messageQueueLength);
    }

    public synchronized void connectToServer(String topic, final IIPCSocketReceiver callback) {
        this.topic = topic;
        this.callback = callback;
        Log.i(TAG, "connectToServer: " + topic);
        if (callback == null) {
            Log.e(TAG, "connectToServer: callback == null");
            return;
        }
        if (server != null && !isDisconnectInitiative) {
            Log.i(TAG, "connectToServer: local socket has connected");
            return;
        }
        server = new LocalSocket();
        try {
            server.connect(new LocalSocketAddress(Constant.HOST_PACKAGE_NAME));
            Log.i(TAG, "subScribeTopic: connect socket successful " + server);
        } catch (Exception e) {
            e.printStackTrace();
            server = null;
            Log.e(TAG, "subScribeTopic: error");
            return;
        }
        startReceiveThread(server, callback);
        startSendThread(server);
        enqueueMessage("connect", topic, null);
    }

    private void startReceiveThread(final LocalSocket server, final IIPCSocketReceiver callback) {
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "client start receive");
                Utils.startReceive(server, callback, TAG);
                Log.w(TAG, "client end receive!!");
                onAbort();
            }
        }, "IPCClientReceive-Thread");
        receiveThread.start();
    }

    private void startSendThread(final LocalSocket server) {
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "client start send");
                Utils.startSend(server, messageQueue, TAG);
                Log.w(TAG, "client end send!!");
                onAbort();
            }
        }, "IPCClientSend-Thread");
        sendThread.start();
    }

    private void onAbort() {
        Log.w(TAG, "onAbort: is initiative: " + isDisconnectInitiative);
        if (!isDisconnectInitiative) {
            shutdown();
            reConnect();
        }
    }

    private void reConnect() {
        Log.i(TAG, "reConnect: ");
        connectToServer(topic, callback);
    }

    public synchronized void disConnectInitiative(String topic) {
        Log.i(TAG, "unSubscribeTopic: " + topic);
        isDisconnectInitiative = true;
        messageQueue.clear();
        messageQueue.offer(new IPCSocketData("disconnect", topic, null));
        while (messageQueue.size() > 0) {
            try {
                Thread.sleep(100);
                Log.w(TAG, "disConnectInitiative: wait for disconnect send");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }

    private void shutdown() {
        Log.i(TAG, "shutdown: ");
        if (receiveThread != null) {
            receiveThread.interrupt();
        }
        if (sendThread != null) {
            sendThread.interrupt();
        }
        if (server != null) {
            try {
                server.shutdownOutput();
                server.shutdownInput();
                server.close();
                server = null;
                Log.i(TAG, "shutdown: server.close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enqueueMessage(final String method, final String topic, final String message) {
        if (isDisconnectInitiative) {
            Log.w(TAG, "enqueueMessage: disconnect initiative");
            return;
        }
        IPCSocketData data = new IPCSocketData(method, topic, message);
        Log.i(TAG, "enqueueMessage: queue size: " + messageQueue.size());
        if (messageQueue.offer(data)) {
            Log.i(TAG, "enqueueMessage to server: " + topic);
        } else {
            Log.w(TAG, "enqueueMessage to server failed message queue size: " + messageQueue.size());
            messageQueue.clear();
            Log.w(TAG, "enqueueMessage: clear!");
        }
    }
}
