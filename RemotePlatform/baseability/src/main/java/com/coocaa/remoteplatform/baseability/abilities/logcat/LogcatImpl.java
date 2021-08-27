package com.coocaa.remoteplatform.baseability.abilities.logcat;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
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

    private static final int MSG_LOGCAT = 4;

    @Override
    public void handleMessage(RemoteCommand command) {
        mCommand = command;
        if (terminalSession == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        }
        File file = new File(mContext.getExternalCacheDir(), "log.txt");
        Log.d(TAG, "file path =" + file.getAbsolutePath());
        //开始抓日志
        if (16 == command.cmdType) {
            Log.d(TAG, "handleMessage: start logcat");
            terminalSession.write("logcat > " + file.getAbsolutePath() + "\r");
            command.replyFinish(mContext).reply();
        } else if (17 == command.cmdType) {  //结束抓日志
            Log.d(TAG, "handleMessage: end logcat");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
