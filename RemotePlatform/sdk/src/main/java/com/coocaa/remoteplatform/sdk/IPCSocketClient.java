package com.coocaa.remoteplatform.sdk;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import com.coocaa.remoteplatform.core.common.Constant;
import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.common.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: TopicHandler
 * @Author: XuZeXiao
 * @CreateDate: 4/14/21 4:48 PM
 * @Description:
 */
public class IPCSocketClient {
    private static final String TAG = "IPCSocketClient";
    private final BlockingQueue<IPCSocketData> messageQueue = new LinkedBlockingQueue<>(5);
    private LocalSocket server = null;
    private final Map<String, IIPCSocketReceiver> socketReceivers = new HashMap<>();
    private IIPCSocketReceiver clientCallback = new IIPCSocketReceiver() {
        @Override
        public void onReceive(String msg, LocalSocket client) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                if (jsonObject == null) {
                    Log.w(TAG, "onReceive: jsonObject null");
                }
                String topic = jsonObject.getString("topic");
                IIPCSocketReceiver receiver = socketReceivers.get(topic);
                if (receiver == null) {
                    Log.w(TAG, "onReceive: not found receiver: " + topic);
                    return;
                }
                Log.i(TAG, "onReceive: dispatch received msg" + topic);
                receiver.onReceive(msg, client);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public IPCSocketClient() {
    }

    public synchronized void subScribeTopic(String topic, final IIPCSocketReceiver callback) {
        Log.i(TAG, "subScribeTopic: " + topic);
        socketReceivers.put(topic, callback);
        connectToServer(clientCallback);
    }

    private void connectToServer(IIPCSocketReceiver callback) {
        if (server != null && server.isConnected()) {
            Log.i(TAG, "connectToServer: local socket connect");
            return;
        }
        server = new LocalSocket();
        try {
            server.connect(new LocalSocketAddress(Constant.HOST_PACKAGE_NAME));
            Log.i(TAG, "subScribeTopic: connect socket successful " + server);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "subScribeTopic: error");
            return;
        }
        startReceiveThread(server, callback);
        startSendThread(server);
    }

    private void startReceiveThread(final LocalSocket server, final IIPCSocketReceiver callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "client start receive socket");
                Utils.startReceive(server, callback, TAG);
                Log.w(TAG, "client end receive socket");
            }
        }, "IPCClientReceive-Thread").start();
    }

    private void startSendThread(final LocalSocket server) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "client start send");
                Utils.startSend(server, messageQueue, TAG);
                Log.w(TAG, "client end send!!");
            }
        }, "IPCClientSend-Thread").start();
    }

    public synchronized void unSubscribeTopic(String topic) {
        Log.i(TAG, "unSubscribeTopic: " + topic);
        socketReceivers.remove(topic);
    }

    public void enqueueMessage(final String method, final String topic, final String message) {
        IPCSocketData data = new IPCSocketData(method, topic, message);
        if (messageQueue.offer(data)) {
            Log.i(TAG, "enqueueMessage to server: " + topic);
        } else {
            Log.w(TAG, "enqueueMessage to server failed message queue size: " + messageQueue.size());
        }
    }
}
