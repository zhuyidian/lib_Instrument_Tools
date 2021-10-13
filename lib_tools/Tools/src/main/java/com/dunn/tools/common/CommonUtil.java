package com.dunn.tools.common;

import com.dunn.tools.log.LogUtil;

/**
 * Author:Administrator
 * Date:2021/10/13 10:27
 * Description:CommonUtil
 */
public class CommonUtil {
    /**
     * 0~100 转换成 0~15 / 0~100 的范围
     * @param targetVolume
     * @param maxValue
     * @return
     */
    public static int convertVolume(int targetVolume, int maxValue) {
        float convertVom = (float) targetVolume / 100 * maxValue;
        //int target = (int) convertVom;
        int target = Math.round(convertVom);
        LogUtil.i("screen1", "targetVolume=" + targetVolume + ", convertVom=" + convertVom + ", target=" + target);
        if (target > maxValue) target = maxValue;
        if (target < 0) target = 0;
        return target;
    }

    /**
     * 0~15 / 0~100 转换成 0~100 的范围
     * @param currentVolume
     * @param maxValue
     * @return
     */
    public static int convertSystemVolume(int currentVolume, int maxValue) {
        float convertVom = (float) currentVolume / maxValue * 100;
        //int target = (int) convertVom;
        int target = Math.round(convertVom);
        LogUtil.i("screen2", "currentVolume=" + currentVolume + ", convertVom=" + convertVom + ", target=" + target);
        if (target > maxValue) target = maxValue;
        if (target < 0) target = 0;
        return target;
    }
}
