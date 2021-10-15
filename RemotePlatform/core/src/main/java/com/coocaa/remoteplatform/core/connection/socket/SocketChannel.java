package com.coocaa.remoteplatform.core.connection.socket;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.coocaa.remoteplatform.core.BuildConfig;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.connection.IChannelReceiveCallback;
import com.coocaa.remoteplatform.core.connection.IConnectChannel;
import com.coocaa.remoteplatform.core.service.AttachInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.remoteplatform.commom.Constant;
import com.remoteplatform.commom.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

import static com.remoteplatform.commom.Constant.CMD_TYPE_CLOSE_SOCKET_CHANNEL;
import static com.remoteplatform.commom.Constant.CMD_TYPE_OPEN_SOCKET_CHANNEL;
import static com.remoteplatform.commom.Constant.COMMAND_SOURCE_SOCKET;
import static com.remoteplatform.commom.SocketUtils.CONNECT_TYPE_TERMINAL_DEVICE;
import static com.remoteplatform.commom.SocketUtils.PARAMS_ACTIVE_ID;
import static com.remoteplatform.commom.SocketUtils.PARAMS_CONNECT_TYPE;
import static com.remoteplatform.commom.SocketUtils.PARAMS_DEVICE_ID;
import static com.remoteplatform.commom.SocketUtils.PARAMS_ID;
import static com.remoteplatform.commom.SocketUtils.PARAMS_MAC;
import static com.remoteplatform.commom.SocketUtils.PARAMS_SOURCE;
import static com.remoteplatform.commom.SocketUtils.createSocketAddress;

/**
 * @ClassName: StompManager
 * @Author: XuZeXiao
 * @CreateDate: 3/11/21 5:37 PM
 * @Description:
 */
public class SocketChannel implements IConnectChannel {
    private static final String TAG = "SocketChannel";
    private static final String COMMAND_DESTINATION = "/client2web_";
    private AttachInfo mAttachInfo;
    private StompClient mStompClient;
    private IChannelReceiveCallback mCallback = null;
    private Context mContext = null;
    private final Queue<RemoteCommand> mWaitingToSend = new LinkedList<>();
    private Gson gson = new Gson();
    private IPCSocketServer mPassThroughService = null;
    private boolean isConnect = false;
    private List<Disposable> disposables = new ArrayList<>();

    public SocketChannel(AttachInfo attachInfo) {
        mAttachInfo = attachInfo;
        mPassThroughService = new IPCSocketServer(this);
        mPassThroughService.startListen();
    }

    @Override
    public void init(Context context) {
        mContext = context;
    }

    @Override
    public void setChannelListener(IChannelReceiveCallback callback) {
        mCallback = callback;
    }

    @Override
    public void handleMessage(RemoteCommand command) {
        command.replyProcessing(mContext).reply();
        LogUtil.i("command", "handleMessage: " + (command != null ? command.msgOrigin : "unknown") +
                "---> [Processing]");
        SocketContent content = gson.fromJson(command.content, SocketContent.class);
        if (!"command".equalsIgnoreCase(content.topicName)) {
            return;
        }
        command.msgOrigin = COMMAND_SOURCE_SOCKET;
        if (CMD_TYPE_OPEN_SOCKET_CHANNEL == command.cmdType) {
            if (connect()) {
                command.replyFinish(mContext).withDeviceId(mAttachInfo.getDeviceId()).reply();
                LogUtil.i("command", "handleMessage: " + (command != null ? command.msgOrigin : "unknown") +
                        "---> [Finish] stomp connect success");
            } else {
                command.replyError(mContext).withDeviceId(mAttachInfo.getDeviceId()).reply();
                LogUtil.i("command", "handleMessage: " + (command != null ? command.msgOrigin : "unknown") +
                        "---> [Error] stomp connect error");
            }
        } else if (CMD_TYPE_CLOSE_SOCKET_CHANNEL == command.cmdType) {
            disConnect();
            command.replyFinish(mContext).withDeviceId(mAttachInfo.getDeviceId()).reply();
            LogUtil.i("command", "handleMessage: " + (command != null ? command.msgOrigin : "unknown") +
                    "---> [Finish] stomp disConnect");
        }
    }

    @Override
    public String getType() {
        return COMMAND_SOURCE_SOCKET;
    }

    public synchronized boolean connect() {
        if (isConnect()) {
            Log.i(TAG, "connect: has connected");
            return true;
        }
        String baseAddress = Constant.getSocketAddress(mContext);
        String address = createSocketAddress(mContext, baseAddress, getParams(mAttachInfo));
        Log.i(TAG, "connect: " + address);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, address);
        Disposable disposable = mStompClient.lifecycle().subscribe(liveCycleEvent);
        Disposable disposable1 = mStompClient.topic(getTopicAddress("command")).subscribeOn(Schedulers.io()).observeOn(Schedulers.computation()).subscribe(commandTopicCallback);
        disposables.add(disposable);
        disposables.add(disposable1);
        mStompClient.connect(getHeaders());
        return true;
    }

    private final Consumer<LifecycleEvent> liveCycleEvent = new Consumer<LifecycleEvent>() {
        @Override
        public void accept(LifecycleEvent lifecycleEvent) throws Exception {
            Log.i(TAG, "lifecycle: " + lifecycleEvent.getType().toString());
            Exception exception = lifecycleEvent.getException();
            if (exception != null) {
                exception.printStackTrace();
            }
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    isConnect = true;
                    break;
                case ERROR:
                case CLOSED:
                    onDisconnect();
                    break;
            }
        }
    };

    private void onDisconnect() {
        isConnect = false;
        clearDisposables();
        RemoteCommand remoteCommand = new RemoteCommand();
        remoteCommand.cmdType = 9;
        remoteCommand.clientId = "com.coocaa.remoteplatform.baseability";
        String json = gson.toJson(remoteCommand);
        mPassThroughService.enqueueMessage("term", new IPCSocketData("data", "term", json));
        mPassThroughService.enqueueMessage("media", new IPCSocketData("data", "media", json));
    }

    private final Consumer<StompMessage> commandTopicCallback = new Consumer<StompMessage>() {
        @Override
        public void accept(StompMessage stompMessage) throws Exception {
            String payLoad = stompMessage.getPayload();
            //Log.i(TAG, "on command receive: " + payLoad);
            LogUtil.i("command", "accept: <---stomp payLoad=" + payLoad);
            if (mCallback != null) {
                RemoteCommand result = com.coocaa.remoteplatform.core.connection.Utils.createRemoteCommandFromJson(payLoad);
                result.msgOrigin = COMMAND_SOURCE_SOCKET;

                //TODO
                if (Constant.CMD_TYPE_HEART_BEAT == result.cmdType) {
                    handleHeartBeat(result);
                    return;
                }
                mCallback.onReceiveCommand(result);
            }
        }
    };

    private void handleHeartBeat(RemoteCommand remoteCommand) {
        Log.i(TAG, "handleHeartBeat: ");
        remoteCommand.replyFinish(mContext).reply();
        LogUtil.i("command", "accept: stomp---> [Finish] heart beat");
    }

    private String getTopicAddress(String topic) {
        String deviceId = mAttachInfo.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        return "/user/" + deviceId + "/" + topic;
    }

    private String getDestination(String topic) {
        return COMMAND_DESTINATION + topic;
    }

    public static Map<String, String> getParams(AttachInfo attachInfo) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAMS_ID, attachInfo.getDeviceId());
        params.put(PARAMS_ACTIVE_ID, attachInfo.getActiveId());
        params.put(PARAMS_DEVICE_ID, attachInfo.getDeviceId());
        params.put(PARAMS_MAC, attachInfo.getMac());
        params.put(PARAMS_CONNECT_TYPE, CONNECT_TYPE_TERMINAL_DEVICE);
        return params;
    }

    private List<StompHeader> getHeaders() {
        List<StompHeader> headerList = new ArrayList<>();
        headerList.add(new StompHeader(PARAMS_SOURCE, "CLIENT"));
        headerList.add(new StompHeader(PARAMS_ACTIVE_ID, mAttachInfo.getActiveId()));
        headerList.add(new StompHeader(PARAMS_DEVICE_ID, mAttachInfo.getDeviceId()));
        return headerList;
    }

//    private void onConnect() {
//        Log.i(TAG, "onConnect: " + isConnect());
//        sendRemainingMessage();
//    }

    public boolean disConnect() {
        Log.w(TAG, "disConnect!!!");
        if (mStompClient != null) {
            mStompClient.disconnect();
            return true;
        }
        return false;
    }

    public boolean isConnect() {
        return mStompClient != null && isConnect;
    }

    @Override
    public boolean replyMessage(RemoteCommand command) {
//        if (isConnect()) {
//            Log.w(TAG, "replyMessage: not connect");
//            addCommandToWaitingList(command);
//            return false;
//        }
        if (mStompClient == null) {
            Log.w(TAG, "replyMessage: client null");
            return false;
        }
        replyMessage("command", command);
        return true;
    }

    private String getReplyString(RemoteCommand command) {
        JsonObject content = null;
        JsonObject jsonObject = null;
        try {
            if (!TextUtils.isEmpty(command.content)) {
                content = JsonParser.parseString(command.content).getAsJsonObject();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
//                e.printStackTrace();
            }
            Log.w(TAG, "getReplyString: parse content to jsonObject failed");
        }
        try {
            jsonObject = gson.toJsonTree(command).getAsJsonObject();
            if (content != null) {
                jsonObject.add("content", content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            Log.e(TAG, "replyMessage: convert to json error");
            return gson.toJson(command);
        }
        return jsonObject.toString();
    }

    private void addCommandToWaitingList(RemoteCommand command) {
        synchronized (mWaitingToSend) {
            Log.i(TAG, "addCommandToWaitingList: " + command);
            mWaitingToSend.add(command);
        }
    }

//    private void sendRemainingMessage() {
//        while (mWaitingToSend.size() > 0) {
//            RemoteCommand command = mWaitingToSend.poll();
//            Log.i(TAG, "sendRemainingMessage: " + command);
//            if (command == null) {
//                continue;
//            }
//            mStompClient.send(getDestination("command"), command.content).subscribe(new CompletableObserver() {
//                @Override
//                public void onSubscribe(@NonNull Disposable d) {
//
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//
//                @Override
//                public void onError(@NonNull Throwable e) {
//
//                }
//            });
//        }
//    }

    public void replyMessage(String topic, RemoteCommand command) {
        if (mStompClient == null) {
            Log.w(TAG, "sendMessageToTopic: stomp null");
            return;
        }
        if (!isConnect()) {
            Log.e(TAG, "replyMessage: not connect: " + topic);
        }
        String replyString = getReplyString(command);
        mStompClient.send(getDestination(topic), replyString).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void subscribeTopic(String topic) {
        if (mStompClient == null || TextUtils.isEmpty(topic)) {
            Log.e(TAG, "subscribeTopic: mStompClient null");
            return;
        }
        Log.i(TAG, "subscribeTopic: " + topic);
        String destPath = getTopicAddress(topic);
        if (mStompClient != null && !TextUtils.isEmpty(mStompClient.getTopicId(destPath))) {
            Log.w(TAG, "subscribeTopic: repeat topic :" + topic);
            return;
        }
        Disposable d = mStompClient.topic(destPath).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<StompMessage>() {
            @Override
            public void accept(StompMessage stompMessage) throws Exception {
                String payLoad = stompMessage.getPayload();
                Log.i(TAG, "onNext: topic: " + topic + " payload: " + payLoad);
                //TODO
//                RemoteCommand result = com.coocaa.remoteplatform.core.connection.Utils.createRemoteCommandFromJson(payLoad);
                RemoteCommand result = gson.fromJson(payLoad, RemoteCommand.class);
                result.msgOrigin = COMMAND_SOURCE_SOCKET;
                IPCSocketData data = new IPCSocketData("data", topic, gson.toJson(result));
                mPassThroughService.enqueueMessage(topic, data);
            }
        });
        disposables.add(d);
    }

    public void unSubscribeTopic(String topic) {
        Log.i(TAG, "unSubscribeTopic: " + topic);
        if (mStompClient == null || TextUtils.isEmpty(topic)) {
            return;
        }
    }

    private void clearDisposables() {
        Log.i(TAG, "clearDisposables: ");
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        disposables.clear();
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy: ");
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        if (mPassThroughService != null) {
            mPassThroughService.stop();
        }
    }
}
