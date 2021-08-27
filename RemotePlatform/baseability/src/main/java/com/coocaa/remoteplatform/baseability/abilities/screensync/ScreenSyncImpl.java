package com.coocaa.remoteplatform.baseability.abilities.screensync;

import android.content.Context;
import android.net.LocalSocket;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.screenshot.ScreenShotImpl;
import com.coocaa.remoteplatform.baseability.common.FileUtils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.coocaa.remoteplatform.core.common.IPCSocketData;
import com.coocaa.remoteplatform.core.common.IIPCSocketReceiver;
import com.coocaa.remoteplatform.sdk.RemotePlatform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.remoteplatform.commom.Constant;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import swaiotos.sal.system.IScreenshotListener;

import static com.coocaa.remoteplatform.baseability.common.FileUtils.imageToBase64;

/**
 * @ClassName: ScreenSync
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:30 PM
 * @Description:
 */
public class ScreenSyncImpl extends AbsAbility {
    private static final String TAG = "ScreenSync";
    private static final String BASE64_PREFIX = "data:image/jpeg;base64,";
    private static final String TOPIC_NAME = "media";
    private Gson gson = new Gson();
    private RemoteCommand command = null;
    private ScheduledExecutorService executors = null;
    private ScreenSyncBean mScreenSyncConfig = null;
    private static final int defaultWidth = 1920;
    private static final int defaultHeight = 1080;
    private static final float defaultFrame = 0.2f;

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void handleMessage(RemoteCommand command) {
        this.command = command;
        mScreenSyncConfig = gson.fromJson(command.content, ScreenSyncBean.class);
        if (Constant.CMD_TYPE_CLOSE_SOCKET_CHANNEL == command.cmdType) {
            disconnect();
            stopCapture();
        } else if (Constant.CMD_TYPE_OPEN_SOCKET_CHANNEL == command.cmdType) {
            connect();
            startCapture();
        }
        command.replyFinish(mContext).reply();
    }

    @Override
    public String getName() {
        return null;
    }

    private void connect() {
        RemotePlatform.getInstance().subScribeTopic(TOPIC_NAME, new IIPCSocketReceiver() {
            @Override
            public void onReceive(String msg, LocalSocket client) {
                IPCSocketData passThroughData = new IPCSocketData(msg);
                RemoteCommand remoteCommand = gson.fromJson(passThroughData.content, RemoteCommand.class);
                handleMessage(remoteCommand);
            }
        });
    }


    private void startCapture() {
        Log.i(TAG, "startCapture");
        if (executors != null) {
            Log.w(TAG, "startCapture: has started");
            stopCapture();
        }
        executors = Executors.newSingleThreadScheduledExecutor();
        executors.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                captureOnce();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopCapture() {
        Log.i(TAG, "stopCapture: ");
        if (executors != null) {
            executors.shutdown();
            executors = null;
        }
    }

    private void captureOnce() {
        Log.i(TAG, "captureOnce");
        ScreenShotImpl.captureOnce(mContext, 1280, 720, new IScreenshotListener() {
            @Override
            public void onScreenshotFailed(int i, String s) {
                ScreenShotImpl.unlock();
                Log.i(TAG, "onScreenshotFailed: " + s);
            }

            @Override
            public void onScreenshotSuccess(String s, int i, int i1) {
                File file = new File(s);
                String newPath = s + ".temp";
                if (file.exists() && FileUtils.renameFile(s, newPath)) {
                    ScreenShotImpl.unlock();
                    Log.d(TAG, "onScreenshotSuccess: " + newPath);
                    String base64 = BASE64_PREFIX + imageToBase64(newPath);
                    RemoteCommand send = command.replyFinish(mContext).withContent(base64).getCommand();
                    send(send);
                } else {
                    ScreenShotImpl.unlock();
                }
            }
        });
    }

    private void send(RemoteCommand command) {
        JsonObject jsonObject = null;
        try {
            jsonObject = new Gson().toJsonTree(command).getAsJsonObject();
            jsonObject.remove("cmdType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return;
        }
        RemotePlatform.getInstance().sendTopicMessage("data", TOPIC_NAME, jsonObject.toString());
    }

    private void disconnect() {
        Log.i(TAG, "disconnect: ");
        RemotePlatform.getInstance().unSubScribeTopic(TOPIC_NAME);
    }
}
