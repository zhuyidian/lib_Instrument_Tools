package com.dunn.tools.brightness;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * 从Android6.0后 应用是不能修改系统屏幕亮度的 所以下面的工具类 一般也用不上我们只能修改应用的亮度。
 * 屏幕亮度有两种调节模式：
 * Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC：值为1，自动调节亮度。
 * Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL：值为0，手动模式。
 */
public class BrightnessUtil {
    private ContentResolver contentResolver;
    private static BrightnessUtil mInstance;
    private Context mContext;

    private BrightnessUtil(Context context) {
        mContext = context;
        contentResolver = context.getContentResolver();
    }

    public synchronized static BrightnessUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BrightnessUtil(context);
        }
        return mInstance;
    }

    public void setScrennManualMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(mContext)) {
                    Intent intent = new Intent(
                            android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS
                    );
                    intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } else {
                    //有了权限，具体的动作
                    int mode = Settings.System.getInt(
                            contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE
                    );
                    if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        Settings.System.putInt(
                                contentResolver,
                                Settings.System.SCREEN_BRIGHTNESS_MODE,
                                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
                        );
                    }
                }
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setScrennAutoMode() {
        try {
            int mode = Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                );
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getScrennMode() {
        try {
            int mode = Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
            );
            return mode;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getScreenBrightness() {
        int defVal = 125;
        return Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                defVal
        );
    }

    public void saveScreenBrightness(int value) {
        setScrennManualMode();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(mContext)) {
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS
                );
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                //有了权限，具体的动作
                Settings.System.putInt(
                        contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        value
                );
            }
        } else {
            Settings.System.putInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    value
            );
        }
    }
}
