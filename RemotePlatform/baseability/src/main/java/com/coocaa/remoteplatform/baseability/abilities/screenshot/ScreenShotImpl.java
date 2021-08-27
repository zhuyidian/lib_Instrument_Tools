package com.coocaa.remoteplatform.baseability.abilities.screenshot;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.coocaa.remoteplatform.baseability.abilities.AbsAbility;
import com.coocaa.remoteplatform.baseability.common.FileUtils;
import com.coocaa.remoteplatform.core.common.RemoteCommand;
import com.remoteplatform.commom.Constant;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import swaiotos.sal.SAL;
import swaiotos.sal.system.IScreenshotListener;
import swaiotos.sal.system.ISystem;

/**
 * @ClassName: ScreenShotImpl
 * @Author: XuZeXiao
 * @CreateDate: 3/31/21 8:29 PM
 * @Description:
 */
public class ScreenShotImpl extends AbsAbility {
    private static final String TAG = "ScreenShotImpl";
    private static CountDownLatch countDownLatch = new CountDownLatch(0);
    private RemoteCommand mCommand = null;

    @Override
    public void handleMessage(final RemoteCommand command) {
        mCommand = command;
        captureOnce(mContext, 1920, 1080, new IScreenshotListener() {
            @Override
            public void onScreenshotFailed(int i, String s) {
                Log.d(TAG, "onScreenshotFailed: ");
                unlock();
                command.replyError(mContext).reply();
            }

            @Override
            public void onScreenshotSuccess(String s, int i, int i1) {
                File file = new File(s);
                String newPath = file.getParent() + "/ScreenShotTemp.jpg";
                if (file.exists() && FileUtils.renameFile(s, newPath)) {
                    Log.d(TAG, "onScreenshotSuccess: " + " path: " + newPath);
                    unlock();
                    String uploadAddress = Constant.getFileAddress(mContext);
                    FileUtils.get().uploadFile(uploadAddress, command.userId, FileUtils.getFileMd5(newPath), 4, newPath, mUploadCallback);
                } else {
                    unlock();
                }
            }
        });
    }


    @Override
    public String getName() {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static synchronized void captureOnce(Context context, int width, int height, final IScreenshotListener listener) {
        Log.i(TAG, "captureOnce start");
        //锁只能由持有者释放。由于截图和回调接口不在同一个线程，如果直接使用ReentrantLock加锁整一个截图过程，会造成死锁。
        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
            countDownLatch = new CountDownLatch(1);

            ISystem iSystem = SAL.getModule(context, SAL.SYSTEM);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getRealMetrics(displayMetrics);
            if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
                iSystem.screenShot(width, height, listener);
            } else {
                iSystem.screenShot(height, width, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            unlock();
        }
        Log.d(TAG, "captureOnce: end");
    }

    public static void unlock() {
        Log.d(TAG, "unlock: " + countDownLatch.getCount());
        countDownLatch.countDown();
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
