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

import java.util.HashMap;
import java.util.Map;
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
    private final Map<LocalSocket, ServerWrapper> mNonTopicWrappers = new HashMap<>();
    private final Map<String, ServerWrapper> mWrappers = new HashMap<>();
    private Thread mListenThread = null;

    public IPCSocketServer(SocketChannel socketChannel) {
        mSocketChannel = socketChannel;
    }

    public interface IPCSocketServerState {
        void onDisconnect(ServerWrapper serverWrapper);
    }

    public static class ServerWrapper {
        private final BlockingQueue<IPCSocketData> mQueue = new LinkedBlockingQueue<>();
        private LocalSocket client = null;
        private String topic = null;
        private Thread sendThread = null;
        private Thread receiveThread = null;
        private boolean isStop = false;
        private IPCSocketServerState mStateCallback = null;

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public ServerWrapper(String topic, LocalSocket socket, IIPCSocketReceiver callback, IPCSocketServerState stateCallback) {
            this.topic = topic;
            this.client = socket;
            this.mStateCallback = stateCallback;
            startReceiveThread(client, callback);
            startSendThread(client, mQueue);
        }

        private void startReceiveThread(final LocalSocket client, IIPCSocketReceiver callback) {
            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.w(TAG, "server start receive socket");
                    Utils.startReceive(client, callback, TAG);
                    Log.w(TAG, "server end receive socket!!");
                    onAbort();
                }
            }, "IPCServerReceive-Thread");
            receiveThread.start();
        }

        private void startSendThread(final LocalSocket client, BlockingQueue<IPCSocketData> queue) {
            sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.w(TAG, "server start send");
                    Utils.startSend(client, queue, TAG);
                    Log.w(TAG, "server end send!!");
                    onAbort();
                }
            }, "IPCServerSend-Thread");
            sendThread.start();
        }

        public void enqueueMessage(IPCSocketData message) {
            if (isStop) {
                Log.w(TAG, "enqueueMessage: is stop!");
                return;
            }
            if (mQueue.offer(message)) {
                Log.i(TAG, "enqueueMessage to client: " + topic);
            } else {
                Log.w(TAG, "enqueueMessage to client failed message queue size: " + mQueue.size());
            }
        }

        private void onAbort() {
            Log.w(TAG, "onAbort: isStop: " + isStop);
            if (!isStop) {
                shutdown();
                if (mStateCallback != null) {
                    mStateCallback.onDisconnect(this);
                    Log.i(TAG, "onAbort: state call back");
                }
            }
        }

        public void stop() {
            isStop = true;
            shutdown();
        }

        private void shutdown() {
            Log.i(TAG, "shutdown: ");
            mQueue.clear();
            if (sendThread != null) {
                sendThread.interrupt();
                sendThread = null;
            }
            if (receiveThread != null) {
                receiveThread.interrupt();
                receiveThread = null;
            }
        }
    }

    public void startListen() {
        mListenThread = new Thread(new Runnable() {
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
                        handleNewClient(client);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        mListenThread.start();
    }

    private void handleNewClient(final LocalSocket client) {
        Log.i(TAG, "handle new Client: " + client);
        if (client == null) {
            return;
        }
        ServerWrapper wrapper = new ServerWrapper(null, client, socketReceiver, new IPCSocketServerState() {
            @Override
            public void onDisconnect(ServerWrapper serverWrapper) {
                synchronized (mNonTopicWrappers) {
                    mNonTopicWrappers.remove(serverWrapper.client);
                }
                synchronized (mWrappers) {
                    mWrappers.remove(serverWrapper.topic);
                }
            }
        });
        synchronized (mNonTopicWrappers) {
            mNonTopicWrappers.put(client, wrapper);
        }
    }

    private final IIPCSocketReceiver socketReceiver = new IIPCSocketReceiver() {
        @Override
        public void onReceive(String msg, LocalSocket client) {
            IPCSocketData data = mGson.fromJson(msg, IPCSocketData.class);
            if (data == null) {
                return;
            }
            Log.i(TAG, "onReceive: " + data.content);
            if ("data".equalsIgnoreCase(data.method)) {
                RemoteCommand command = mGson.fromJson(data.content, RemoteCommand.class);
                mSocketChannel.replyMessage(data.topic, command);
            } else if ("connect".equalsIgnoreCase(data.method)) {
                handleConnect(data.topic, client);
            } else if ("disconnect".equalsIgnoreCase(data.method)) {
                handleDisconnect(data.topic, client);
            }
        }
    };

    private void handleConnect(String topic, LocalSocket client) {
        Log.i(TAG, "handleConnect: " + topic);
        mSocketChannel.subscribeTopic(topic);
        ServerWrapper wrapper = null;
        synchronized (mNonTopicWrappers) {
            wrapper = mNonTopicWrappers.get(client);
        }
        if (wrapper == null) {
            return;
        }
        wrapper.setTopic(topic);
        synchronized (mWrappers) {
            mWrappers.put(topic, wrapper);
        }
        synchronized (mNonTopicWrappers) {
            mNonTopicWrappers.remove(client);
        }
    }

    private void handleDisconnect(String topic, LocalSocket client) {
        Log.i(TAG, "handleDisconnect: " + topic);
        mSocketChannel.unSubscribeTopic(topic);
        ServerWrapper wrapper = null;
        synchronized (mWrappers) {
            wrapper = mWrappers.get(topic);
        }
        if (wrapper == null) {
            return;
        }
        wrapper.stop();
        synchronized (mWrappers) {
            mWrappers.remove(topic);
        }
    }

    public void enqueueMessage(String topic, IPCSocketData message) {
        ServerWrapper wrapper = null;
        synchronized (mWrappers) {
            wrapper = mWrappers.get(topic);
        }
        if (wrapper != null) {
            wrapper.enqueueMessage(message);
        } else {
            Log.w(TAG, "enqueueMessage: wrapper not found: " + topic);
        }
    }

    public void stop() {
        if (mListenThread != null) {
            mListenThread.interrupt();
            mListenThread = null;
        }
        if (mSocketService != null) {
            try {
                mSocketService.close();
                mSocketService = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        synchronized (mWrappers) {
            for (ServerWrapper value : mWrappers.values()) {
                value.stop();
            }
            mWrappers.clear();
        }
        synchronized (mNonTopicWrappers) {
            for (ServerWrapper value : mNonTopicWrappers.values()) {
                value.stop();
            }
            mNonTopicWrappers.clear();
        }
    }
}
