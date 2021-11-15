package com.dunn.tools.framework.wms;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.List;

/**
 * Author:Administrator
 * Date:2021/10/27 14:41
 * Description:WmsUtil
 */
public class WmsUtil {

    /**
     * 概述：得到当前的屏幕尺寸<br/>
     *
     * @param context
     * @return Display
     * @date 2013-10-22
     */
    public static Display getCurrentDisplay(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        return display;
    }

    /**
     * 概述：得到当前density<br/>
     *
     * @param context
     * @return float
     * @date 2013-12-20
     */
    public static float getDisplayDensity(Context context) {
        dm = context.getResources().getDisplayMetrics();
        float density = dm.density;
        return density;
    }

    /**
     * 概述：得到屏幕的宽度<br/>
     *
     * @param context
     * @return int
     * @date 2013-10-22
     */
    public static int getDisplayWidth(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        if (display == null) {
            return 1920;
        }
        return display.getWidth();
    }

    /**
     * 概述：得到屏幕的高度<br/>
     *
     * @param context
     * @return int
     * @date 2013-10-22
     */
    public static int getDisplayHeight(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        return display.getHeight();
    }

    private static DisplayMetrics dm = null;
    private synchronized static DisplayMetrics getDisplayMetrics() {
        if (dm == null)
            dm = new DisplayMetrics();
        dm.setToDefaults();
        return dm;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        DisplayMetrics dm = getDisplayMetrics();
        return (int) (pxValue / dm.density + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        DisplayMetrics dm = getDisplayMetrics();
        return (int) (dipValue * dm.density + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        DisplayMetrics dm = getDisplayMetrics();
        return (int) (pxValue / dm.scaledDensity + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        DisplayMetrics dm = getDisplayMetrics();
        return (int) (spValue * dm.scaledDensity + 0.5f);
    }
}
