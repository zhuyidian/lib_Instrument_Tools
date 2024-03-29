package com.dunn.instrument.tools.framework.fps;

import android.util.Log;

import com.dunn.instrument.tools.shell.ShellUtils;

public class FpsInfo {
    private static final String TAG = "FpsInfo";
    private static long startTime = 0L;
    private static int lastFrameNum = 0;
    private static boolean ok = true;

    /**
     * get frame per second
     *
     * @return frame per second
     */
    public static float fps() {
        if (ok) {
            long nowTime = System.nanoTime();
            float f = (float) (nowTime - startTime) / 1000000.0F;
            startTime = nowTime;
            int nowFrameNum = getFrameNum();
            final float fps = Math.round((nowFrameNum - lastFrameNum) * 1000
                    / f);
            lastFrameNum = nowFrameNum;
            return fps;
        } else {
            return -1;
        }

    }

    /**
     * get frame value
     *
     * @return frame value
     */
    public static int getFrameNum() {
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand("service call SurfaceFlinger 1013" + "\n", false);
        Log.i("FpsInfo", commandResult.toString());
        String str1 = commandResult.successMsg;
        if (str1 != null) {
            int start = str1.indexOf("(");
            int end = str1.indexOf("  ");
            if ((start != -1) & (end > start)) {
                String str2 = str1.substring(start + 1, end);
                return Integer.parseInt((String) str2, 16);
            }
        }
        ok = false;
        return -1;
    }
}
