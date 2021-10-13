package com.coocaa.remoteplatform.baseability.abilities.logcat;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.abilities.terminal.ClientConnect;
import com.coocaa.remoteplatform.baseability.abilities.terminal.TerminalSession;
import com.coocaa.remoteplatform.baseability.common.FileUtils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.remoteplatform.commom.Constant;

import java.io.File;

/**
 * @ClassName: LogcatImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:28 PM
 * @Description:
 */
public class LogcatImpl extends AbsAbility {
    private static final String TAG = "LogcatImpl";
    private RemoteCommand mCommand = null;
    //由于LogcatImpl被实例化两次，要确保TerminalSession唯一性
    private static TerminalSession terminalSession;
    private static ClientConnect clientConnect;
    private static boolean isTelnetEnable = true;
    private File file;

    private static final int MSG_LOGCAT = 4;

    @Override
    public void handleMessage(RemoteCommand command) {
        mCommand = command;
        file = new File(mContext.getExternalCacheDir(), "log.txt");
        Log.d(TAG, "file path =" + file.getAbsolutePath());
        if (isTelnetEnable) {
            if (clientConnect == null) {
                clientConnect = new ClientConnect(new ClientConnect.SessionChangedCallback() {
                    @Override
                    public void onSessionStart() {
                        //开始抓日志
                        Log.d(TAG, "handleMessage: start logcat by telnet");
                        clientConnect.write("logcat > " + file.getAbsolutePath() + "\r");
                    }

                    @Override
                    public void onSessionFinished(String error) {
                        Log.d(TAG, "onSessionFinished: " + error);
                        //无法连接telnet
                        if (error.contains("SocketDisconnect-1")) {
                            isTelnetEnable = false;
                            initTerminalSession(); //由于连接telnet属于异步，这里可以保证telnet连接失败时启动terminal抓取日志
                        }
                    }

                    @Override
                    public void onTextChanged(String text) {
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

        if (16 == mCommand.cmdType) {
            mCommand.replyFinish(mContext).reply();
        }

        //结束抓日志
        if (17 == command.cmdType) {
            Log.d(TAG, "handleMessage: end logcat");
            if (isTelnetEnable) {
                clientConnect.write(new byte[]{0X03}, 0, 1);//ctrl + c
                clientConnect.finishIfRunning();
                clientConnect = null;
                mMainThreadHandler.sendMessageDelayed(mMainThreadHandler.obtainMessage(MSG_LOGCAT), 1000);
            } else {
                terminalSession.writeCodePoint(false, 3); //ctrl + c
                terminalSession.finishIfRunning();
                terminalSession = null;
            }
        }
    }

    @Override
    public String getName() {
        return null;
    }

    final Handler mMainThreadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: ");
            if (msg.what == MSG_LOGCAT) {
                File file = new File(mContext.getExternalCacheDir(), "log.txt");
                if (file.exists()) {
                    Log.d(TAG, "handleMessage: file:" + file.getAbsolutePath());
                    String uploadAddress = Constant.getFileAddress(mContext);
                    FileUtils.get().uploadFile(uploadAddress, mCommand.userId, FileUtils.getFileMd5(file.getAbsolutePath()), 17, file.getAbsolutePath(), mUploadCallback);
                } else {
                    mCommand.replyError(mContext).reply();
                }
            }
        }
    };

    private void initTerminalSession() {
        if (terminalSession == null) {
            terminalSession = new TerminalSession("/system/bin/sh", "/", null, null, new TerminalSession.SessionChangedCallback() {
                @Override
                public void onTextChanged(TerminalSession changedSession, String text) {

                }

                @Override
                public void onTitleChanged(TerminalSession changedSession) {

                }

                @Override
                public void onSessionFinished(TerminalSession finishedSession) {
                    //避免数据没有写入就上传
                    mMainThreadHandler.sendMessageDelayed(mMainThreadHandler.obtainMessage(MSG_LOGCAT), 1000);
                    Log.d(TAG, "onSessionFinished: ");
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
            terminalSession.initializeEmulator(1000, 100);
            Log.d(TAG, "handleMessage: start logcat by terminal");
            terminalSession.write("logcat > " + file.getAbsolutePath() + "\r");
        }
    }

    private final FileUtils.IFileUpLoadCallback mUploadCallback = new FileUtils.IFileUpLoadCallback() {

        @Override
        public void onProgress() {
            if (mCommand != null) {
                mCommand.replyProcessing(mContext).reply();
            }
        }

        @Override
        public void onResult(boolean isSuccess, String msg) {
            FileUtils.deleteFile(new File(mContext.getExternalCacheDir(), "log.txt").getAbsolutePath());
            if (mCommand != null) {
                if (isSuccess) {
                    mCommand.replyFinish(mContext).reply();
                } else {
                    mCommand.replyError(mContext).reply();
                }
            }
        }

    };
}
